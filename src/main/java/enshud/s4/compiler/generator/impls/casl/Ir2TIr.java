package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.Register;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.*;
import enshud.s4.compiler.generator.impls.casl.tir.operand.*;
import enshud.s4.compiler.generator.impls.casl.tir.program.*;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.instructions.*;
import enshud.s4.compiler.ir.instructions.Call;
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
    private Map<String, Integer> variableLevelMap;
    private Map<IrFunction, FrameInfo> frameInfoMap;

    public TIRProgram convert(IrProgram irProgram) {
        this.variableLevelMap = generateVariableLevelMap(irProgram);
        this.frameInfoMap = generateFrameInfoMap(irProgram);
        var main = convert(irProgram.main);
        var functions = irProgram.functions.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convert(e.getValue())
                ));
        return new TIRProgram(main, functions);
    }

    private TIRFunction convert(IrFunction irFunction) {
        var frameInfo = frameInfoMap.get(irFunction);
        Ir2TIrConverter visitor = new Ir2TIrConverter(frameInfoMap, irFunction);
        var instructions = visitor.convertInstructions();
        var constDataList = visitor.getConstDataList();

        var blocks = CFGBuilder.build(instructions);

        return new TIRFunction(
                irFunction.name,
                frameInfo,
                blocks,
                constDataList
        );
    }

    private Map<String, Integer> generateVariableLevelMap(IrProgram irProgram) {
        Map<String, Integer> variableLevelMap = new java.util.HashMap<>();
        for (var entry : irProgram.functions.entrySet()) {
            var function = entry.getValue();
            for (var varEntry : function.variables.entrySet()) {
                variableLevelMap.put(varEntry.getKey(), function.level);
            }
            for (var paramEntry : function.parameters.entrySet()) {
                variableLevelMap.put(paramEntry.getKey(), function.level);
            }
        }
        for (var varEntry : irProgram.main.variables.entrySet()) {
            variableLevelMap.put(varEntry.getKey(), irProgram.main.level);
        }
        for (var paramEntry : irProgram.main.parameters.entrySet()) {
            variableLevelMap.put(paramEntry.getKey(), irProgram.main.level);
        }
        return variableLevelMap;
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
    private final SerialManager serialManager;
    private final Map<IrFunction, FrameInfo> frameInfoMap;
    private final IrFunction currentFunction;
    private final List<ConstData<?>> constDataList;
    private final SerialManager constDataSerialManager;

    public Ir2TIrConverter(Map<IrFunction, FrameInfo> frameInfoMap, IrFunction currentFunction) {
        this.tirInstructions = new ArrayList<>();
        this.serialManager = new SerialManager("LL");
        this.constDataSerialManager = new SerialManager("CD");
        this.frameInfoMap = frameInfoMap;
        this.currentFunction = currentFunction;
        this.constDataList = new ArrayList<>();
    }

    public List<TIrInstr<?>> convertInstructions() {
        for (var irInstruction : currentFunction.getInstructions()) {
            irInstruction.accept(this);
        }
        tirInstructions.add(new Ret());
        return tirInstructions;
    }

    public List<ConstData<?>> getConstDataList() {
        return constDataList;
    }

    @Override
    public Void visit(Call ir) {
        ir.args.forEach(arg -> {
            tirInstructions.add(new Push(
                    convertOperand(arg)
            ));
        });
        tirInstructions.add(new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call(ir.functionLabel));
        return null;
    }

    @Override
    public Void visit(Jump ir) {
        switch (ir.type) {
            case UNCONDITIONAL -> {
                tirInstructions.add(new Jmp(ir.target, Jmp.Type.UNCONDITIONAL));
            }
            case TRUE ->  {
                tirInstructions.add(new Cp(convertOperand(ir.condition), Immediate.FALSE));
                tirInstructions.add(new Jmp(ir.target, Jmp.Type.NONZERO));
            }
            case FALSE -> {
                tirInstructions.add(new Cp(convertOperand(ir.condition), Immediate.FALSE));
                tirInstructions.add(new Jmp(ir.target, Jmp.Type.ZERO));
            }
        }
        return null;
    }

    @Override
    public Void visit(Label ir) {
        Nop nop = new Nop().setLabel(ir.name);
        nop.setLabel(ir.name);
        tirInstructions.add(nop);
        return null;
    }

    @Override
    public Void visit(LoadIndexed ir) {
        var slotOpt = lookupVariableSlot(ir.arrayVar);
        tirInstructions.addAll(List.of(
            new Ld(convertOperand(ir.targetVar), new FrameSlot(slotOpt.slot, 0).setOffset(convertOperand(ir.indexVar)))
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
            new St(convertOperand(ir.value), new FrameSlot(slotOpt.slot, 0).setOffset(convertOperand(ir.index)))
        ));
        return null;
    }

    @Override
    public Void visit(Write ir) {
        List<TIrInstr<?>> instr = switch (ir.writeType){
            case INT -> {
                yield List.of(
                        move(CaslRegisters.GR2, ir.operand),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("WRTINT")
                );
            }
            case CHAR -> {
                yield List.of(
                        move(CaslRegisters.GR2, ir.operand),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("WRTCH")
                );
            }
            case STRING -> {
                yield List.of(
                        new Lad(new RealRegister(CaslRegisters.GR7), new Address("BUF")),
                        new Ld(RealRegister.of(CaslRegisters.GR1), new Address("-1").setOffset(convertOperand(ir.operand))),
                        new Lad(RealRegister.of(CaslRegisters.GR6), new NumericImmediate(0, NumericImmediate.Type.DECIMAL)),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("WRTSTR")
                );
            }
            case NEWLINE -> {
                yield List.of(
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("WRTLN")
                );
            }
        };
        tirInstructions.addAll(instr);
        return null;
    }

    @Override
    public Void visit(BinaryOp ir) {
        switch(ir.opType){
            case ADD -> {
                tirInstructions.add(new Add(
                        convertOperand(ir.left),
                        convertOperand(ir.right)
                ));
            }
            case SUB -> {
                tirInstructions.add(new Sub(
                        convertOperand(ir.left),
                        convertOperand(ir.right)
                ));
            }
            case MUL -> {
                tirInstructions.addAll(List.of(
                        move(CaslRegisters.GR1, ir.left),
                        move(CaslRegisters.GR2, ir.right),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("MULT"),
                        new Ld(convertOperand(ir.left), RealRegister.of(CaslRegisters.GR2))
                ));
            }
            case DIV -> {
                tirInstructions.addAll(List.of(
                        move(CaslRegisters.GR1, ir.left),
                        move(CaslRegisters.GR2, ir.right),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("DIV"),
                        new Ld(convertOperand(ir.left), RealRegister.of(CaslRegisters.GR2))
                ));
            }
            case MOD -> {
                tirInstructions.addAll(List.of(
                        move(CaslRegisters.GR1, ir.left),
                        move(CaslRegisters.GR2, ir.right),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("DIV"),
                        new Ld(convertOperand(ir.left), RealRegister.of(CaslRegisters.GR1))
                ));
            }
            case AND -> {
                tirInstructions.add(
                        new And(convertOperand(ir.left), convertOperand(ir.right))
                );
            }
            case OR -> { // boolean OR
                tirInstructions.add(
                        new Or(convertOperand(ir.left), convertOperand(ir.right))
                );
            }
            case XOR -> {
                tirInstructions.add(
                        new Xor(convertOperand(ir.left), convertOperand(ir.right))
                );
            }
            case NEQ -> {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Cp(convertOperand(ir.left), convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.ZERO),
                        new Lad(convertOperand(ir.left), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(ir.left), Immediate.FALSE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case EQ ->  {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Cp(convertOperand(ir.left), convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.NONZERO),
                        new Lad(convertOperand(ir.left), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(ir.left), Immediate.FALSE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case LTE ->   {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(convertOperand(ir.left), convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.PLUS),
                        new Lad(convertOperand(ir.left), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(ir.left), Immediate.FALSE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case LT ->    {
                var labelTrue = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(convertOperand(ir.left), convertOperand(ir.right)),
                        new Jmp(labelTrue, Jmp.Type.MINUS),
                        new Lad(convertOperand(ir.left), Immediate.FALSE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(ir.left), Immediate.TRUE).setLabel(labelTrue),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case GTE ->   {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(convertOperand(ir.left), convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.MINUS),
                        new Lad(convertOperand(ir.left), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(ir.left), Immediate.FALSE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case GT ->    {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(convertOperand(ir.left), convertOperand(ir.right)),
                        new Jmp(labelFalse, Jmp.Type.PLUS),
                        new Lad(convertOperand(ir.left), Immediate.FALSE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(ir.left), Immediate.TRUE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            default -> throw new UnsupportedOperationException("Unsupported binary operation: " + ir.opType);
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

    private TIrInstr<?> move(Register dest, Operand src) {
        return switch (src) {
            case Const c -> {
                yield new Lad(new RealRegister(dest), convertOperand(src));
            }
            default -> {
                yield new Ld(new RealRegister(dest), convertOperand(src));
            }
        };
    }

    private FrameSlot lookupVariableSlot(Variable var) {
        var func = currentFunction;
        while(func != null){
            var slotOpt = frameInfoMap.get(func).getSlot(var.name);
            if(slotOpt.isPresent()){
                var slot = slotOpt.get();
                var levelDiff = currentFunction.level - func.level;
                return new FrameSlot(slot.slot, levelDiff);
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
            case Temp t -> new VirtualRegister(t.id);
            case Variable v -> {
                yield lookupVariableSlot(v);
            }
            default -> throw new IllegalStateException("Unexpected value: " + irOperand);
        };
    }
}
