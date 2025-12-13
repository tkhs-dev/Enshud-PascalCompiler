package enshud.s4.compiler.generator.impls.casl;

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
import enshud.s4.compiler.generator.impls.casl.tir.program.TIRFunction;
import enshud.s4.compiler.generator.impls.casl.tir.program.TIRProgram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssemblyGenerator {
    private final TIRProgram program;
    private final List<AssemblyInstr<?>> instructions;

    public AssemblyGenerator(TIRProgram program) {
        this.program = program;
        this.instructions = new ArrayList<>();
        instructions.add(new START("BEGIN").setLabel("CASL"));
    }

    public List<AssemblyInstr<?>> generate() {
        // Process main function
        processFunction(program.main);
        instructions.get(1).setLabel("BEGIN");
        // Process other functions
        for (var function : program.functions.values()) {
            processFunction(function);
        }
        instructions.add(new END());
        return instructions;
    }

    private void processFunction(TIRFunction function) {
        var blocks = function.getBlocks();
        var analyzer = new LivenessAnalyzer<>(blocks);
        analyzer.analyze();
        var desc = new CaslDescription();
        var interferenceGraph = InterferenceGraph.build(blocks, desc, RealRegister::of);
        var allocator = new RegisterAllocator(interferenceGraph, desc);
        var allocation = allocator.allocate();

        var converter = new TIRConverter(allocation, function);
        var funcInstructions = converter.getInstructions();
        instructions.addAll(funcInstructions);
    }
}

class TIRConverter implements ITIRVisitor<Void>{
    private final List<AssemblyInstr<?>> instructions;
    private final Allocation allocation;
    private final TIRFunction function;

    public TIRConverter(Allocation allocation, TIRFunction function) {
        this.instructions = new ArrayList<>();
        this.allocation = allocation;
        this.function = function;
    }

    public List<AssemblyInstr<?>> getInstructions() {
        for (var ir : function.getInstructions()) {
            ir.accept(this);
        }
        return instructions;
    }

    @Override
    public Void visit(Add ir) {
        addInstructions(ir,
                new ADDA(reg(ir.left),  data(ir.right))
        );
        return null;
    }

    @Override
    public Void visit(And ir) {
        addInstructions(ir,
                new AND(reg(ir.left),  data(ir.right))
        );
        return null;
    }

    @Override
    public Void visit(Call ir) {

        return null;
    }

    @Override
    public Void visit(Cp ir) {
        addInstructions(ir,
                new CPA(reg(ir.left),  data(ir.right))
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
                new LAD(reg(ir.dest), mem(ir.value))
        );
        return null;
    }

    @Override
    public Void visit(Ld ir) {
        addInstructions(ir,
                new LD(reg(ir.dest), data(ir.src))
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
                new OR(reg(ir.left),  data(ir.right))
        );
        return null;
    }

    @Override
    public Void visit(Pop ir) {
        addInstructions(ir,
                new POP(reg(ir.target))
        );
        return null;
    }

    @Override
    public Void visit(Push ir) {
        addInstructions(ir,
                new PUSH(mem(ir.src))
        );
        return null;
    }

    @Override
    public Void visit(Ret ir) {
        addInstructions(ir,
                new RET()
        );
        return null;
    }

    @Override
    public Void visit(Sl ir) {
        addInstructions(ir,
                new SLA(reg(ir.left), data(ir.right))
        );
        return null;
    }

    @Override
    public Void visit(Sr ir) {
        addInstructions(ir,
                new SRA(reg(ir.left), data(ir.right))
        );
        return null;
    }

    @Override
    public Void visit(St ir) {
        addInstructions(ir,
                new ST(reg(ir.src), mem(ir.dest))
        );
        return null;
    }

    @Override
    public Void visit(Sub ir) {
        addInstructions(ir,
                new SUBA(reg(ir.left),  data(ir.right))
        );
        return null;
    }

    @Override
    public Void visit(Xor ir) {
        addInstructions(ir,
                new XOR(reg(ir.left),  data(ir.right))
        );
        return null;
    }

    private void addInstructions(TIrInstr<?> ir, AssemblyInstr<?>... instrs) {
        if(ir.getLabel().isPresent()){
            instrs[0].setLabel(ir.getLabel().get());
        }
        Collections.addAll(instructions, instrs);
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
                yield new RegisterOperand(reg);
            }
            case enshud.s4.compiler.generator.impls.casl.tir.operand.RealRegister r -> {
                yield new RegisterOperand(r.register);
            }
            case enshud.s4.compiler.generator.impls.casl.tir.operand.Immediate<?> i -> {
                yield switch(i) {
                    case NumericImmediate n -> new MemoryOperand(String.valueOf(n.value));
                    case CharImmediate c -> new MemoryOperand(String.valueOf(c.value));
                    case StringImmediate n -> new MemoryOperand(String.valueOf(n.value));
                    default -> throw new IllegalArgumentException("Unknown immediate type: " + i.value);
                };
            }
            case FrameSlot f -> {
                if (f.offset == null) {
                    yield new MemoryOperand(String.valueOf(f.slot), CaslRegisters.FP);
                }
                if (f.offset instanceof Immediate i) {
                    if(i instanceof NumericImmediate n){
                        instructions.add(new LAD(new RegisterOperand(CaslRegisters.TEMP), new MemoryOperand(String.valueOf(n.value))));
                    } else {
                        throw new IllegalArgumentException("FrameSlot offset must be NumericImmediate: " + i);
                    }
                } else {
                    instructions.add(new LD(new RegisterOperand(CaslRegisters.TEMP), data(f.offset)));
                }
                yield new MemoryOperand(String.valueOf(f.slot), CaslRegisters.FP);
            }
            default -> throw new IllegalArgumentException("Unknown operand type: " + operand);
        };
    }
}
