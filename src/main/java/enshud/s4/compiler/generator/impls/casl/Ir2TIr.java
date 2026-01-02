package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.Register;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.*;
import enshud.s4.compiler.generator.impls.casl.tir.operand.*;
import enshud.s4.compiler.generator.impls.casl.tir.program.*;
import enshud.s4.compiler.instruction.InstructionSet;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.instructions.Call;
import enshud.s4.compiler.ir.instructions.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;
import enshud.s4.compiler.ir.program.IrFunction;
import enshud.s4.compiler.ir.program.IrProgram;
import enshud.util.SerialManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ir2TIr {
    private Map<String, Integer> funcLevelMap;
    private Map<IrFunction, FrameInfo> frameInfoMap;

    public TIRProgram convert(IrProgram irProgram) {
        this.funcLevelMap = irProgram.functions.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        e -> e.getValue().label,
                        e -> e.getValue().level
                ));
        this.frameInfoMap = generateFrameInfoMap(irProgram);
        var main = convert(irProgram.main);
        main.getEditor().prependAll(true, List.of(
                new Lad(VirtualRegister.withPredefined("WRTGR6", CaslRegisters.GR6).withSpillCost(0), new Address("0")),
                new Lad(VirtualRegister.withPredefined("WRTGR7", CaslRegisters.GR7).withSpillCost(0), new Address("BUF"))
        ));
        var functions = irProgram.functions.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convert(e.getValue())
                ));
        return new TIRProgram(main, functions);
    }

    private TIRFunction convert(IrFunction irFunction) {
        var frameInfo = frameInfoMap.get(irFunction);
        Ir2TIrConverter visitor = new Ir2TIrConverter(frameInfoMap, funcLevelMap, irFunction);
        var instructions = visitor.convertInstructions();
        var constDataList = visitor.getConstDataList();

        return new TIRFunction(
                irFunction.name,
                frameInfo,
                instructions,
                constDataList
        );
    }

    private static Map<IrFunction, FrameInfo> generateFrameInfoMap(IrProgram irProgram) {
        Map<IrFunction, FrameInfo> frameInfoMap = new java.util.HashMap<>();
        for (var entry : irProgram.functions.entrySet()) {
            var function = entry.getValue();
            frameInfoMap.put(function, generateFrameInfo(function));
        }
        frameInfoMap.put(irProgram.main, generateFrameInfo(irProgram.main));
        return frameInfoMap;
    }

    private static FrameInfo generateFrameInfo(IrFunction irFunction) {
        FrameInfo frameInfo = new FrameInfo();
        for (var entry : irFunction.variables.entrySet()) {
            var type = entry.getValue();
            if (type instanceof enshud.pascal.type.ArrayType arrType) {
                frameInfo.addLocalVar(entry.getKey(), arrType.getSize());
            } else {
                frameInfo.addLocalVar(entry.getKey(), 1); // assuming non-array types take 1 slot
            }
        }
        for (var entry : irFunction.parameters.entrySet()) {
            var type = entry.getValue();
            if (type instanceof enshud.pascal.type.ArrayType arrType) {
                frameInfo.addParam(entry.getKey(), arrType.getSize());
            } else {
                frameInfo.addParam(entry.getKey(), 1); // assuming non-array types take 1 slot
            }
        }
        return frameInfo;
    }
}

class Ir2TIrConverter implements IIrVisitor<Void> {
    private final List<TIrInstr<?>> tirInstructions;
    private final Map<IrFunction, FrameInfo> frameInfoMap;
    private final Map<String, Integer> funcLevelMap;
    private final IrFunction currentFunction;
    private final List<ConstData<?>> constDataList;
    private final Map<String, String> labelMap;
    private final Map<String, VirtualRegister> tempRegisterMap;

    private static final SerialManager serialManager = new SerialManager("L");
    private static final SerialManager constDataSerialManager = new SerialManager("CD");
    private final SerialManager tempRegisterSerialManager = new SerialManager("t");

    private static final VirtualRegister WRTGR6 = VirtualRegister.withPredefined("WRTGR6", CaslRegisters.GR6).withSpillCost(0);
    private static final VirtualRegister WRTGR7 = VirtualRegister.withPredefined("WRTGR7", CaslRegisters.GR7).withSpillCost(0);

    public Ir2TIrConverter(Map<IrFunction, FrameInfo> frameInfoMap, Map<String, Integer> funcLevelMap, IrFunction currentFunction) {
        this.tirInstructions = new ArrayList<>();
        this.frameInfoMap = frameInfoMap;
        this.funcLevelMap = funcLevelMap;
        this.currentFunction = currentFunction;
        this.constDataList = new ArrayList<>();
        this.labelMap = new java.util.HashMap<>();
        this.tempRegisterMap = new java.util.HashMap<>();
    }

    public InstructionSet<TIrInstr<?>> convertInstructions() {
        tirInstructions.add(new Prologue());
        for (var irInstruction : currentFunction.getInstructions()) {
            irInstruction.accept(this);
        }
        tirInstructions.add(new Epilogue());
        tirInstructions.add(new Ret());
        return InstructionSet.of(tirInstructions);
    }

    public List<ConstData<?>> getConstDataList() {
        return constDataList;
    }

    @Override
    public Void visit(Call ir) {
        // push arguments
        ir.args.reversed().forEach(arg -> {
                    tirInstructions.add(
                            new Push(new Address("0").setOffset(convertOperand(arg)))
                    );
                });
        // push static link
        var levelDiff = currentFunction.level - funcLevelMap.get(ir.functionLabel);
        if (levelDiff ==  -1) {
            tirInstructions.add(
                    new Push(new Address("0").setOffset(VirtualRegister.tmpWithPredefined(CaslRegisters.FP)))
            );
        }else{
            var staticLink = VirtualRegister.tmpWithPredefined(CaslRegisters.GR2);
            tirInstructions.addAll(List.of(
                    new Ld(staticLink, new FrameSlot(0, levelDiff)),
                    new Push(new Address("0").setOffset(staticLink))
            ));
        }
        tirInstructions.add(new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call(ir.functionLabel,
                true,
                List.of(WRTGR6, WRTGR7),
                List.of(WRTGR6, WRTGR7)
        ));
        // pop static link
        tirInstructions.add(
                new Pop(VirtualRegister.tmpWithPredefined(CaslRegisters.GR2))
        );
        // pop arguments
        if (!ir.args.isEmpty()) {
            tirInstructions.add(
                    new Lad(
                            VirtualRegister.tmpWithPredefined(CaslRegisters.SP),
                            new Address(String.valueOf(ir.args.size())).setOffset(VirtualRegister.tmpWithPredefined(CaslRegisters.SP))
                    )
            );
        }

        return null;
    }

    @Override
    public Void visit(Jump ir) {
        var targetLabel = mapLabel(ir.target);
        switch (ir.type) {
            case UNCONDITIONAL -> {
                tirInstructions.add(new Jmp(targetLabel, Jmp.Type.UNCONDITIONAL));
            }
            case TRUE ->  {
                tirInstructions.add(new Cp(convertOperand(ir.condition), Immediate.FALSE));
                tirInstructions.add(new Jmp(targetLabel, Jmp.Type.NONZERO));
            }
            case FALSE -> {
                tirInstructions.add(new Cp(convertOperand(ir.condition), Immediate.FALSE));
                tirInstructions.add(new Jmp(targetLabel, Jmp.Type.ZERO));
            }
        }
        return null;
    }

    @Override
    public Void visit(Label ir) {
        if (ir.name.startsWith("F")){
            // 関数の先頭ラベルの場合、Prologue命令の後ろにラベルをつける
            tirInstructions.getFirst().setLabel(ir.name);
        }else{
            tirInstructions.add(new Nop().withLabel(mapLabel(ir.name)));
        }
        return null;
    }

    @Override
    public Void visit(LoadIndexed ir) {
        var slotOpt = lookupVariableSlot(ir.arrayVar);
        tirInstructions.addAll(List.of(
            new Ld(convertOperand(ir.targetVar), new FrameSlot(slotOpt.slot, slotOpt.levelDiff).setOffset(convertOperand(ir.indexVar)))
        ));
        return null;
    }

    @Override
    public Void visit(Move ir) {
        tirInstructions.add(move(ir.dest, ir.src));
        return null;
    }

    @Override
    public Void visit(StoreIndexed ir) {
        var slotOpt = lookupVariableSlot(ir.target);
        tirInstructions.addAll(List.of(
            new St(convertOperand(ir.value), new FrameSlot(slotOpt.slot, slotOpt.levelDiff).setOffset(convertOperand(ir.index)))
        ));
        return null;
    }

    @Override
    public Void visit(Write ir) {
//        tirInstructions.addAll(List.of(
//                new Push(new Address("0").setOffset(VirtualRegister.tmpWithPredefined(CaslRegisters.GR6))),
//                new Push(new Address("0").setOffset(VirtualRegister.tmpWithPredefined(CaslRegisters.GR7))),
//                new Ld(VirtualRegister.tmpWithPredefined(CaslRegisters.GR6), new Address("WRTGR6")),
//                new Ld(VirtualRegister.tmpWithPredefined(CaslRegisters.GR7), new Address("WRTGR7"))
//        ));
        List<TIrInstr<?>> instr = switch (ir.writeType){
            case INT -> {
                yield List.of(
                        move(CaslRegisters.GR2, ir.operand),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call(
                                "WRTINT",
                                false,
                                List.of(WRTGR6, WRTGR7),
                                List.of(WRTGR6, WRTGR7)
                        )
                );
            }
            case CHAR -> {
                yield List.of(
                        move(CaslRegisters.GR2, ir.operand),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call(
                                "WRTCH",
                                false,
                                List.of(WRTGR6, WRTGR7),
                                List.of(WRTGR6, WRTGR7)
                        )
                );
            }
            case STRING -> {
                yield List.of(
                        new Ld(VirtualRegister.tmpWithPredefined(CaslRegisters.GR1), new Address("-1").setOffset(convertOperand(ir.operand))),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call(
                                "WRTSTR",
                                false,
                                List.of(WRTGR6, WRTGR7),
                                List.of(WRTGR6, WRTGR7)
                        )
                );
            }
            case NEWLINE -> {
                yield List.of(
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call(
                                "WRTLN",
                                false,
                                List.of(WRTGR6, WRTGR7),
                                List.of(WRTGR6, WRTGR7)
                        )
                );
            }
        };
        tirInstructions.addAll(instr);
//        tirInstructions.addAll(List.of(
//                new St(VirtualRegister.tmpWithPredefined(CaslRegisters.GR7), new Address("WRTGR7")),
//                new St(VirtualRegister.tmpWithPredefined(CaslRegisters.GR6), new Address("WRTGR6")),
//                new Pop(VirtualRegister.tmpWithPredefined(CaslRegisters.GR7)),
//                new Pop(VirtualRegister.tmpWithPredefined(CaslRegisters.GR6))
//        ));
        return null;
    }

    @Override
    public Void visit(BinaryOp ir) {
        TIROperand leftOperand;
        if(ir.left instanceof Temp){
            leftOperand = convertOperand(ir.left);
        }else{
            leftOperand = new VirtualRegister(tempRegisterSerialManager.New());
            tirInstructions.add(
                    move(leftOperand, ir.left)
            );
        }
        switch(ir.opType){
            case ADD -> {
                tirInstructions.addAll(List.of(
                    new Add(
                        leftOperand,
                        convertOperand(ir.right)
                    ),
                    new Ld(convertOperand(ir.result), leftOperand)
                ));
            }
            case SUB -> {
                tirInstructions.addAll(List.of(
                        new Sub(
                                leftOperand,
                                convertOperand(ir.right)
                        ),
                        new Ld(convertOperand(ir.result), leftOperand)
                ));
            }
            case MUL -> {
                var in1 = VirtualRegister.tmpWithPredefined(CaslRegisters.GR1);
                var in2 = VirtualRegister.tmpWithPredefined(CaslRegisters.GR2);
                var out = VirtualRegister.tmpWithPredefined(CaslRegisters.GR2);
                tirInstructions.addAll(List.of(
                        move(in1, leftOperand),
                        move(in2, ir.right),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("MULT",
                                false,
                                List.of(in1, in2),
                                List.of(out)
                        ),
                        new Ld(convertOperand(ir.result), out)
                ));
            }
            case DIV -> {
                var in1 = VirtualRegister.tmpWithPredefined(CaslRegisters.GR1);
                var in2 = VirtualRegister.tmpWithPredefined(CaslRegisters.GR2);
                var out = VirtualRegister.tmpWithPredefined(CaslRegisters.GR2);
                tirInstructions.addAll(List.of(
                        move(in1, leftOperand),
                        move(in2, ir.right),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("DIV",
                                false,
                                List.of(in1, in2),
                                List.of(out)
                        ),
                        new Ld(convertOperand(ir.result), out)
                ));
            }
            case MOD -> {
                var in1 = VirtualRegister.tmpWithPredefined(CaslRegisters.GR1);
                var in2 = VirtualRegister.tmpWithPredefined(CaslRegisters.GR2);
                var out = VirtualRegister.tmpWithPredefined(CaslRegisters.GR1);
                tirInstructions.addAll(List.of(
                        move(in1, leftOperand),
                        move(in2, ir.right),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("DIV",
                                false,
                                List.of(in1, in2),
                                List.of(out)
                        ),
                        new Ld(convertOperand(ir.result), out)
                ));
            }
            case AND -> {
                tirInstructions.addAll(List.of(
                        new And(
                                leftOperand,
                                convertOperand(ir.right)
                        ),
                        new Ld(convertOperand(ir.result), leftOperand)
                ));
            }
            case OR -> { // boolean OR
                tirInstructions.addAll(List.of(
                        new Or(
                                leftOperand,
                                convertOperand(ir.right)
                        ),
                        new Ld(convertOperand(ir.result), leftOperand)
                ));
            }
            case NEQ -> {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Cp(leftOperand, convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.ZERO),
                        new Ld(convertOperand(ir.result), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Ld(convertOperand(ir.result), Immediate.FALSE).withLabel(labelFalse),
                        new Nop().withLabel(labelEnd)
                ));
            }
            case EQ ->  {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Cp(leftOperand, convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.NONZERO),
                        new Ld(convertOperand(ir.result), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Ld(convertOperand(ir.result), Immediate.FALSE).withLabel(labelFalse),
                        new Nop().withLabel(labelEnd)
                ));
            }
            case LTE ->   {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(leftOperand, convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.PLUS),
                        new Ld(convertOperand(ir.result), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Ld(convertOperand(ir.result), Immediate.FALSE).withLabel(labelFalse),
                        new Nop().withLabel(labelEnd)
                ));
            }
            case LT ->    {
                var labelTrue = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(leftOperand, convertOperand(ir.right)),
                        new Jmp(labelTrue, Jmp.Type.MINUS),
                        new Ld(convertOperand(ir.result), Immediate.FALSE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Ld(convertOperand(ir.result), Immediate.TRUE).withLabel(labelTrue),
                        new Nop().withLabel(labelEnd)
                ));
            }
            case GTE ->   {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(leftOperand, convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.MINUS),
                        new Ld(convertOperand(ir.result), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Ld(convertOperand(ir.result), Immediate.FALSE).withLabel(labelFalse),
                        new Nop().withLabel(labelEnd)
                ));
            }
            case GT ->    {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(leftOperand, convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.PLUS),
                        new Ld(convertOperand(ir.result), Immediate.FALSE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Ld(convertOperand(ir.result), Immediate.TRUE).withLabel(labelFalse),
                        new Nop().withLabel(labelEnd)
                ));
            }
            default -> throw new UnsupportedOperationException("Unsupported binary operation: " + ir.opType);
        }
        return null;
    }

    @Override
    public Void visit(UnaryOp ir) {
        switch (ir.opType) {
            case NEG -> {
                tirInstructions.addAll(List.of(
                        new Lad(
                                convertOperand(ir.result),
                                Immediate.ZERO
                        ),
                        new Sub(
                                convertOperand(ir.result),
                                convertOperand(ir.operand)
                        )
                ));
            }
            case NOT -> {
                tirInstructions.addAll(List.of(
                        new Xor(
                                convertOperand(ir.operand),
                                new Address("TRUE")
                        ),
                        new Ld(
                                convertOperand(ir.result),
                                convertOperand(ir.operand)
                        )
                ));
            }
            default -> throw new UnsupportedOperationException("Unsupported unary operation: " + ir.opType);
        }
        return null;
    }

    @Override
    public Void visit(Load ir) {
        tirInstructions.add(new Ld(convertOperand(ir.targetVar), convertOperand(ir.var)));
        return null;
    }

    @Override
    public Void visit(Store ir) {
        tirInstructions.add(new St(convertOperand(ir.value), convertOperand(ir.target)));
        return null;
    }

    private String mapLabel(String originalLabel) {
        return labelMap.computeIfAbsent(originalLabel, k -> serialManager.New());
    }

    private TIrInstr<?> move(Operand dest, Operand src) {
        return switch (src) {
            case Const c -> {
                yield new Lad(convertOperand(dest), convertOperand(src));
            }
            default -> {
                yield new Ld(convertOperand(dest), convertOperand(src));
            }
        };
    }

    private TIrInstr<?> move(TIROperand dest, Operand src) {
        return switch (src) {
            case Const c -> {
                yield new Lad(dest, convertOperand(src));
            }
            default -> {
                yield new Ld(dest, convertOperand(src));
            }
        };
    }
    private TIrInstr<?> move(Register dest, Operand src) {
        return switch (src) {
            case Const c -> {
                yield new Lad(VirtualRegister.tmpWithPredefined(dest), convertOperand(src));
            }
            default -> {
                yield new Ld(VirtualRegister.tmpWithPredefined(dest), convertOperand(src));
            }
        };
    }
    private TIrInstr<?> move(TIROperand dest, TIROperand src) {
        return new Ld(dest, src);
    }

    private FrameSlot lookupVariableSlot(Variable var) {
        var func = currentFunction;
        while(func != null){
            var slotOpt = frameInfoMap.get(func).getSlot(var.name);
            if(slotOpt.isPresent()){
                var slot = slotOpt.get();
                if (slot.kind == FrameInfo.FrameSlot.Kind.LOCAL) {
                    // Local variable
                    var levelDiff = currentFunction.level - func.level;
                    return new FrameSlot(slot.slot, levelDiff);
                } else {
                    // Parameter
                    var levelDiff = currentFunction.level - func.level;
                    return new FrameSlot(-slot.slot, levelDiff); // パラメタは負のオフセットに格納
                }
            }
            func = func.parent;
        }
        throw new RuntimeException("Variable not found in any frame: " + var.name);
    }

    private TIROperand convertOperand(Operand irOperand) {
        return switch(irOperand){
            case Const c ->{
                if (c.value instanceof Integer i){
                    yield new NumericImmediate(i, NumericImmediate.Type.DECIMAL);
                }else if(c.value instanceof Boolean b){
                    yield new NumericImmediate(b ? 1 : 0, NumericImmediate.Type.DECIMAL);
                }else if(c.value instanceof Character ch){
                    yield new CharImmediate(ch);
                }else if(c.value instanceof String s){
                    var label = constDataSerialManager.New();
                    this.constDataList.add(new StringData(
                            label,
                            s
                    ));
                    yield new StringImmediate(label);
                }else {
                    throw new UnsupportedOperationException("Unsupported constant type: " + c.value.getClass() + " value: " + c.value);
                }
            }
            case Temp t ->{
                var tempReg = tempRegisterMap.computeIfAbsent(t.id, k -> new VirtualRegister(tempRegisterSerialManager.New()));
                yield tempReg;
            }
            case Variable v -> {
                yield lookupVariableSlot(v);
            }
            default -> throw new IllegalStateException("Unexpected value: " + irOperand);
        };
    }
}
