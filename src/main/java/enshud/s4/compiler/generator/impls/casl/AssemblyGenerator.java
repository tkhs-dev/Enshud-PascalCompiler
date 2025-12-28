package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.InterferenceGraph;
import enshud.s4.compiler.generator.LivenessAnalyzer;
import enshud.s4.compiler.generator.allocate.Allocation;
import enshud.s4.compiler.generator.allocate.RegisterAllocator;
import enshud.s4.compiler.generator.impls.casl.assembly.instructions.*;
import enshud.s4.compiler.generator.impls.casl.assembly.operand.AssemblyOperand;
import enshud.s4.compiler.generator.impls.casl.assembly.operand.DataOperand;
import enshud.s4.compiler.generator.impls.casl.assembly.operand.MemoryOperand;
import enshud.s4.compiler.generator.impls.casl.assembly.operand.RegisterOperand;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.*;
import enshud.s4.compiler.generator.impls.casl.tir.operand.*;
import enshud.s4.compiler.generator.impls.casl.tir.program.ConstData;
import enshud.s4.compiler.generator.impls.casl.tir.program.StringData;
import enshud.s4.compiler.generator.impls.casl.tir.program.TIRFunction;
import enshud.s4.compiler.generator.impls.casl.tir.program.TIRProgram;
import enshud.s4.compiler.instruction.InstructionSet;
import enshud.s4.compiler.instruction.InstructionSetEditor;
import enshud.util.Ascii;
import enshud.util.SerialManager;

import java.util.List;
import java.util.Set;

public class AssemblyGenerator {
    private final TIRProgram program;
    private final InstructionSet<AssemblyInstr<?>> instructions;
    private final InstructionSetEditor<AssemblyInstr<?>> editor;
    private SerialManager serial;

    public AssemblyGenerator(TIRProgram program) {
        this.program = program;
        this.instructions = new InstructionSet<>();
        this.editor = instructions.getEditor();
    }

    public InstructionSet<AssemblyInstr<?>> generate() {
        editor.append(
                new START("BEGIN").withLabel("CASL"),
                new LD(new RegisterOperand(CaslRegisters.FP), new RegisterOperand(CaslRegisters.SP))
        );
        // Process main function
        processFunction(program.main);
        editor.applyAt(1, ir -> ir.setLabel("BEGIN"));
        // Process other functions
        for (var function : program.functions.values()) {
            processFunction(function);
        }
        processConstData(program.getConstDataList());
        editor.append(
                new DS(256).withLabel("BUF"),
                new DC(1).withLabel("TRUE"),
                new DC(0).withLabel("FALSE"),
                new DC(0).withLabel("ZERO"),
                new DC(1).withLabel("ONE"),
                new DS(1).withLabel("WRTGR6"),
                new DS(1).withLabel("WRTGR7"),
                new END()
        );
        return instructions;
    }

    private void processFunction(TIRFunction function) {
        serial = new SerialManager("spill");
        var desc = new CaslDescription();
        Allocation allocation = null;
        while(true){
            var blocks = function.getBlocks();
            new LivenessAnalyzer<>(blocks).analyze();
            var interferenceGraph = InterferenceGraph.build(blocks, desc, VirtualRegister::tmpWithPredefined);
            var allocator = new RegisterAllocator(interferenceGraph, desc);
            allocation = allocator.allocate();
            var spilled = allocation.getSpilled();
            System.out.println("Spilling registers: " + spilled);
            if(spilled.isEmpty()) {
                break;
            }
            spillFunction(function, spilled);
            System.out.println("After spilling:");
            System.out.println(function);
        }
        System.out.println(allocation);

        var converter = new TIRConverter(allocation, function);
        var funcInstructions = converter.getInstruction();
        editor.appendAll(funcInstructions);
    }

    private void spillFunction(TIRFunction function, Set<AllocatableRegister> spilled) {
        var editor = function.getEditor();
        for (var vreg : spilled) {
            var slot = function.frameInfo.addSpill(1);
            var frameSlot = new FrameSlot(slot, 0);
            while(editor.hasNext()){
                var ir = editor.next();
                var uses = ir.getUse();
                var defs = ir.getDef();
                if(uses.contains(vreg) && defs.contains(vreg)){
                    // 両方にある場合は一時レジスタを使う
                    var loadRegister = new VirtualRegister(serial.New(), 2);
                    if(vreg.getPredefined().isPresent()) {
                        // 元が事前割り当てレジスタの場合は事前割り当てを引き継ぐ
                        loadRegister.withPredefined(vreg.getPredefined().get());
                    }
                    editor.insertBefore(
                            new Ld(loadRegister, frameSlot)
                    );
                    ir.replaceUse(vreg, loadRegister);
                    ir.replaceDef(vreg, loadRegister);
                    editor.insertAfter(
                            new St(loadRegister, frameSlot)
                    );
                }else{
                    if(uses.contains(vreg)){
                        var register = new VirtualRegister(serial.New(), 2);
                        if(vreg.getPredefined().isPresent()) {
                            // 元が事前割り当てレジスタの場合は事前割り当てを引き継ぐ
                            register.withPredefined(vreg.getPredefined().get());
                        }
                        editor.insertBefore(
                                new Ld(register, frameSlot)
                        );
                        ir.replaceUse(vreg, register);
                    }
                    if(defs.contains(vreg)) {
                        var register = new VirtualRegister(serial.New());
                        if (vreg.getPredefined().isPresent()) {
                            // 元が事前割り当てレジスタの場合は事前割り当てを引き継ぐ
                            register.withPredefined(vreg.getPredefined().get());
                        }
                        ir.replaceDef(vreg, register);
                        editor.insertAfter(
                                new St(register, frameSlot)
                        );
                    }
                }
            }
            editor.resetToStart();
        }
    }

    private void processConstData(List<ConstData<?>> constDataList) {
        for (var constData : constDataList) {
            if (constData instanceof StringData strData) {
                editor.append(
                        new DC(strData.value.length()),
                        new DC(strData.value).withLabel(strData.label)
                );
            }
        }
    }
}

class TIRConverter implements ITIRVisitor<Void>{
    private final InstructionSet instructions;
    private final InstructionSetEditor<AssemblyInstr<?>> editor;
    private final Allocation allocation;
    private final TIRFunction function;

    public TIRConverter(Allocation allocation, TIRFunction function) {
        this.instructions = new InstructionSet();
        this.editor = instructions.getEditor();
        this.allocation = allocation;
        this.function = function;
    }

    public InstructionSet<AssemblyInstr<?>> getInstruction() {
        for (var ir : function.getInstructions()) {
            ir.accept(this);
        }
        constructFrame();
        return instructions;
    }

    private void constructFrame(){
        var frame = function.frameInfo;
        editor.prepend(true,
                new PUSH(new MemoryOperand("0", CaslRegisters.FP)),
                new LD(new RegisterOperand(CaslRegisters.FP), new RegisterOperand(CaslRegisters.SP)),
                new PUSH(new MemoryOperand("0")), // 静的リンク
                new LAD(new RegisterOperand(CaslRegisters.TEMP), new MemoryOperand(String.valueOf(-frame.getFrameSize()))),
                new ADDA(new RegisterOperand(CaslRegisters.SP), new RegisterOperand(CaslRegisters.TEMP))
        );
    }

    private void destructFrame(){
        editor.append(
                new LD(new RegisterOperand(CaslRegisters.SP), new RegisterOperand(CaslRegisters.FP)),
                new POP(new RegisterOperand(CaslRegisters.FP))
        );
    }

    @Override
    public Void visit(Add ir) {
        addInstructions(ir,
                new ADDA(reg(ir.getLeft()),  data(ir.getRight()))
        );
        return null;
    }

    @Override
    public Void visit(And ir) {
        addInstructions(ir,
                new AND(reg(ir.getLeft()),  data(ir.getRight()))
        );
        return null;
    }

    @Override
    public Void visit(Call ir) {
        addInstructions(ir,
                new CALL(ir.functionName)
        );
        return null;
    }

    @Override
    public Void visit(Cp ir) {
        addInstructions(ir,
                new CPA(reg(ir.getLeft()),  data(ir.getRight()))
        );
        return null;
    }

    @Override
    public Void visit(Jmp ir) {
        switch(ir.type){
            case PLUS -> {
                addInstructions(ir, new JPL(ir.addressLabel));
            }
            case MINUS -> {
                addInstructions(ir, new JMI(ir.addressLabel));
            }
            case ZERO -> {
                addInstructions(ir, new JZE(ir.addressLabel));
            }
            case NONZERO -> {
                addInstructions(ir, new JNZ(ir.addressLabel));
            }
            case OVERFLOW -> {
                addInstructions(ir, new JOV(ir.addressLabel));
            }
            case UNCONDITIONAL -> {
                addInstructions(ir, new JUMP(ir.addressLabel));
            }
        }
        return null;
    }

    @Override
    public Void visit(Lad ir) {
        addInstructions(ir,
                new LAD(reg(ir.getDest()), mem(ir.getValue()))
        );
        return null;
    }

    @Override
    public Void visit(Ld ir) {
        addInstructions(ir,
                new LD(reg(ir.getDest()), data(ir.getSrc()))
        );
        return null;
    }

    @Override
    public Void visit(Nop ir) {
        addInstructions(ir, new NOP());
        return null;
    }

    @Override
    public Void visit(Or ir) {
        addInstructions(ir,
                new OR(reg(ir.getLeft()),  data(ir.getRight()))
        );
        return null;
    }

    @Override
    public Void visit(Pop ir) {
        addInstructions(ir,
                new POP(reg(ir.getTarget()))
        );
        return null;
    }

    @Override
    public Void visit(Push ir) {
        addInstructions(ir,
                new PUSH(mem(ir.getSrc()))
        );
        return null;
    }

    @Override
    public Void visit(Ret ir) {
        destructFrame();
        addInstructions(ir,
                new RET()
        );
        return null;
    }

    @Override
    public Void visit(Sl ir) {
        addInstructions(ir,
                new SLA(reg(ir.getLeft()), data(ir.getRight()))
        );
        return null;
    }

    @Override
    public Void visit(Sr ir) {
        addInstructions(ir,
                new SRA(reg(ir.getLeft()), data(ir.getRight()))
        );
        return null;
    }

    @Override
    public Void visit(St ir) {
        addInstructions(ir,
                new ST(reg(ir.getSrc()), mem(ir.getDest()))
        );
        return null;
    }

    @Override
    public Void visit(Sub ir) {
        addInstructions(ir,
                new SUBA(reg(ir.getLeft()),  data(ir.getRight()))
        );
        return null;
    }

    @Override
    public Void visit(Xor ir) {
        addInstructions(ir,
                new XOR(reg(ir.getLeft()),  data(ir.getRight()))
        );
        return null;
    }

    private void addInstructions(TIrInstr<?> ir, AssemblyInstr<?>... instrs) {
        if(ir.getLabel().isPresent()){
            instrs[0].withLabel(ir.getLabel().get());
        }
        editor.append(instrs);
    }

    private RegisterOperand reg(TIROperand operand) {
        var op = convertOperand(operand);
        if (op instanceof RegisterOperand r) {
            return r;
        }
        throw new IllegalArgumentException("Operand is not a register: " + operand);
    }

    private MemoryOperand mem(TIROperand operand) {
        var op = convertOperand(operand);
        if (op instanceof MemoryOperand m) {
            return m;
        }
        throw new IllegalArgumentException("Operand is not a memory operand: " + operand);
    }

    private DataOperand data(TIROperand operand) {
        var op = convertOperand(operand);
        if (op instanceof DataOperand d) {
            return d;
        }
        throw new IllegalArgumentException("Operand is not a data operand: " + operand);
    }

    private AssemblyOperand convertOperand(TIROperand operand) {
        return switch (operand) {
            case enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister r -> {
                var reg = allocation.getAllocationMap().get(r);
                if (reg == null) {
                    throw new IllegalArgumentException("Register not allocated: " + r);
                }
                yield new RegisterOperand(reg);
            }
            case enshud.s4.compiler.generator.impls.casl.tir.operand.Immediate<?> i -> {
                yield switch(i) {
                    case NumericImmediate n -> new MemoryOperand(String.valueOf(n.value));
                    case CharImmediate c -> new MemoryOperand(String.valueOf(Ascii.toAscii(c.value)));
                    case StringImmediate n -> new MemoryOperand(String.valueOf(n.value));
                    default -> throw new IllegalArgumentException("Unknown immediate type: " + i.value);
                };
            }
            case FrameSlot f -> {
                var slotPtr = f.slot >= 0 ? -f.slot - 1 : -f.slot + 1; // 負の場合は仮引数なのでフレームより前
                var baseFrameReg = CaslRegisters.FP;
                if(f.levelDiff > 0){
                    // 静的リンクをたどる
                    editor.append(
                        new LD(new RegisterOperand(CaslRegisters.TEMP), new MemoryOperand("-1", CaslRegisters.FP))
                    );
                    for(int i = 1; i < f.levelDiff; i++){
                        editor.append(
                            new LD(new RegisterOperand(CaslRegisters.TEMP), new MemoryOperand("-1", CaslRegisters.TEMP))
                        );
                    }
                    baseFrameReg = CaslRegisters.TEMP;
                }
                if (f.offset == null) {
                    yield new MemoryOperand(String.valueOf(slotPtr), baseFrameReg);
                }
                if (f.offset instanceof Immediate i) {
                    if(i instanceof NumericImmediate n){
                        editor.append(new LAD(new RegisterOperand(CaslRegisters.TEMP), new MemoryOperand(String.valueOf(-(n.value - 1)), baseFrameReg))); // 配列のインデックスが1始まりのため1をたす
                    } else {
                        throw new IllegalArgumentException("FrameSlot offset must be NumericImmediate: " + i);
                    }
                } else {
                    editor.append(
                        new LAD(new RegisterOperand(CaslRegisters.TEMP), new MemoryOperand("1", baseFrameReg)),
                        new SUBA(new RegisterOperand(CaslRegisters.TEMP), data(f.offset))
                    ); // 配列のインデックスが1始まりのため1をたす
                }
                yield new MemoryOperand(String.valueOf(slotPtr), CaslRegisters.TEMP);
            }
            case Address a -> {
                if (a.offset == null) {
                    yield new MemoryOperand(a.base);
                } else {
                    if (a.offset instanceof Immediate i) {
                        if(i instanceof NumericImmediate n){
                            editor.append(new LAD(new RegisterOperand(CaslRegisters.TEMP), new MemoryOperand(String.valueOf(n.value))));
                        } else {
                            throw new IllegalArgumentException("Address offset must be NumericImmediate: " + i);
                        }
                    } else {
                        editor.append(new LD(new RegisterOperand(CaslRegisters.TEMP), data(a.offset)));
                    }
                    yield new MemoryOperand(a.base, CaslRegisters.TEMP);
                }
            }
            default -> throw new IllegalArgumentException("Unknown operand type: " + operand);
        };
    }
}
