package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.InterferenceGraph;
import enshud.s4.compiler.generator.LivenessAnalyzer;
import enshud.s4.compiler.generator.allocate.RegisterAllocator;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.*;
import enshud.s4.compiler.generator.impls.casl.tir.operand.*;
import enshud.s4.compiler.generator.impls.casl.tir.program.CFGBuilder;
import enshud.s4.compiler.generator.impls.casl.tir.program.FrameInfo;
import enshud.s4.compiler.generator.impls.casl.tir.program.TIRFunction;
import enshud.s4.compiler.generator.impls.casl.tir.program.TIRProgram;
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
    public TIRProgram convert(IrProgram irProgram) {
        var main = convert(irProgram.main);
        var functions = irProgram.functions.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convert(e.getValue())
                ));
        return new TIRProgram(main, functions);
    }

    private TIRFunction convert(IrFunction irFunction) {
        var frameInfo = generateFrameInfo(irFunction);
        Ir2TIrVisitor visitor = new Ir2TIrVisitor(frameInfo);
        for (var irInstruction : irFunction.getInstructions()) {
            irInstruction.accept(visitor);
        }
        var instructions = visitor.getInstructions();
        var blocks = CFGBuilder.build(instructions);
        var analyzer = new LivenessAnalyzer<>(blocks);
        analyzer.analyze();
        var desc = new CaslDescription();
        var interferenceGraph = InterferenceGraph.build(blocks, desc, RealRegister::of);
        var allocator = new RegisterAllocator(interferenceGraph, desc);
        var allocation = allocator.allocate();
        System.out.println(allocation);

        return new TIRFunction(
                irFunction.name,
                frameInfo,
                blocks
        );
    }

    private FrameInfo generateFrameInfo(IrFunction irFunction) {
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

class Ir2TIrVisitor implements IIrVisitor<Void> {
    private final List<Instr<?>> tirInstructions;
    private final SerialManager serialManager;
    private final FrameInfo frameInfo;

    public Ir2TIrVisitor(FrameInfo frameInfo) {
        this.tirInstructions = new ArrayList<>();
        this.serialManager = new SerialManager("LL");
        this.frameInfo = frameInfo;
    }

    public List<Instr<?>> getInstructions() {
        return tirInstructions;
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
        var slotOpt = frameInfo.getSlot(ir.arrayVar.name);
        if (slotOpt.isEmpty()) {
            throw new RuntimeException("Variable not found in frame: " + ir.arrayVar.name);
        }
        tirInstructions.addAll(List.of(
            new Ld(convertOperand(ir.targetVar), new FrameSlot(slotOpt.get().slot).setOffset(convertOperand(ir.indexVar)))
        ));
        return null;
    }

    @Override
    public Void visit(Move ir) {
        tirInstructions.add(new Lad(convertOperand(ir.dest), convertOperand(ir.src)));
        return null;
    }

    @Override
    public Void visit(StoreIndexed ir) {
        var slotOpt = frameInfo.getSlot(ir.target.name);
        if (slotOpt.isEmpty()) {
            throw new RuntimeException("Variable not found in frame: " + ir.target.name);
        }
        tirInstructions.addAll(List.of(
            new St(convertOperand(ir.value), new FrameSlot(slotOpt.get().slot).setOffset(convertOperand(ir.index)))
        ));
        return null;
    }

    @Override
    public Void visit(Write ir) {
        List<Instr<?>> instr = switch (ir.writeType){
            case INT -> {
                yield List.of(
                        new Lad(RealRegister.of(CaslRegisters.GR2), convertOperand(ir.operand)),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("WRTINT")
                );
            }
            case CHAR -> {
                yield List.of(
                        new Lad(RealRegister.of(CaslRegisters.GR2), convertOperand(ir.operand)),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("WRTCH")
                );
            }
            case STRING -> {
                var operand = convertOperand(ir.operand);
                yield List.of(
                        new Ld(RealRegister.of(CaslRegisters.GR1), operand),
                        new Lad(RealRegister.of(CaslRegisters.GR2), convertOperand(ir.operand)),
                        new Add(RealRegister.of(CaslRegisters.GR2), new NumericImmediate(1, NumericImmediate.Type.DECIMAL)),
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
                        new Lad(RealRegister.of(CaslRegisters.GR1), convertOperand(ir.left)),
                        new Lad(RealRegister.of(CaslRegisters.GR2), convertOperand(ir.right)),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("MULT"),
                        new Lad(convertOperand(ir.left), RealRegister.of(CaslRegisters.GR2))
                ));
            }
            case DIV -> {
                tirInstructions.addAll(List.of(
                        new Lad(RealRegister.of(CaslRegisters.GR1), convertOperand(ir.left)),
                        new Lad(RealRegister.of(CaslRegisters.GR2), convertOperand(ir.right)),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("DIV"),
                        new Lad(convertOperand(ir.left), RealRegister.of(CaslRegisters.GR2))
                ));
            }
            case MOD -> {
                tirInstructions.addAll(List.of(
                        new Lad(RealRegister.of(CaslRegisters.GR1), convertOperand(ir.left)),
                        new Lad(RealRegister.of(CaslRegisters.GR2), convertOperand(ir.right)),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("DIV"),
                        new Lad(convertOperand(ir.left), RealRegister.of(CaslRegisters.GR1))
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
                    yield new StringImmediate(s);
                }else {
                    throw new UnsupportedOperationException("Unsupported constant type: " + c.value.getClass() + " value: " + c.value);
                }
            }
            case Temp t -> new VirtualRegister(t.id);
            case Variable v -> {
                var slotOpt = frameInfo.getSlot(v.name);
                if (slotOpt.isEmpty()) {
                    throw new RuntimeException("Variable not found in frame: " + v.name);
                }
                var slot = slotOpt.get();
                yield new FrameSlot(slot.slot);
            }
            default -> throw new IllegalStateException("Unexpected value: " + irOperand);
        };
    }
}
