package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.impls.casl.tir.instructions.*;
import enshud.s4.compiler.generator.impls.casl.tir.operand.*;
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

import java.io.Serial;
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
        var data = convert(irProgram.data);
        return new TIRProgram(main, functions, data);
    }

    public TIRFunction convert(IrFunction irFunction) {
        Ir2TIrVisitor visitor = new Ir2TIrVisitor();
        for (var irInstruction : irFunction.getInstructions()) {
            irInstruction.accept(visitor);
        }
        return new TIRFunction(
                irFunction.name,
                visitor.getFrameInfo(),
                visitor.getInstructions()
        );
    }
}

class Ir2TIrVisitor implements IIrVisitor<Void> {
    private final List<TIRInstruction> tirInstructions;
    private final FrameInfo frameInfo;
    private final SerialManager serialManager;

    public Ir2TIrVisitor() {
        this.tirInstructions = new ArrayList<>();
        this.frameInfo = new FrameInfo();
        this.serialManager = new SerialManager("LL");
    }

    public List<TIRInstruction> getInstructions() {
        return tirInstructions;
    }

    public FrameInfo getFrameInfo() {
        return frameInfo;
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
    public Void visit(Decl ir) {
        if(ir.isParam) {
            frameInfo.addParam(ir.varName, 1);
        } else {
            frameInfo.addLocalVar(ir.varName, 1);
        }
        return null;
    }

    @Override
    public Void visit(DeclArray ir) {
        int size =  ir.upperBound - ir.lowerBound + 1;
        if(ir.isParam) {
            frameInfo.addParam(ir.varName, size);
        } else {
            frameInfo.addLocalVar(ir.varName, size);
        }
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
        return null;
    }

    @Override
    public Void visit(Move ir) {
        tirInstructions.add(new Lad(convertOperand(ir.dest), convertOperand(ir.src)));
        return null;
    }

    @Override
    public Void visit(StoreIndexed ir) {
        return null;
    }

    @Override
    public Void visit(Writeln writeln) {
        return null;
    }

    @Override
    public Void visit(BinaryOp binaryOp) {
        switch(binaryOp.opType){
            case ADD -> {
                tirInstructions.add(new Add(
                        convertOperand(binaryOp.left),
                        convertOperand(binaryOp.right)
                ));
            }
            case SUB -> {
                tirInstructions.add(new Sub(
                        convertOperand(binaryOp.left),
                        convertOperand(binaryOp.right)
                ));
            }
            case MUL -> {
                tirInstructions.addAll(List.of(
                        new Lad(RealRegister.of(Registers.GR1), convertOperand(binaryOp.left)),
                        new Lad(RealRegister.of(Registers.GR2), convertOperand(binaryOp.right)),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("MULT"),
                        new Lad(convertOperand(binaryOp.left), RealRegister.of(Registers.GR2))
                ));
            }
            case DIV -> {
                tirInstructions.addAll(List.of(
                        new Lad(RealRegister.of(Registers.GR1), convertOperand(binaryOp.left)),
                        new Lad(RealRegister.of(Registers.GR2), convertOperand(binaryOp.right)),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("DIV"),
                        new Lad(convertOperand(binaryOp.left), RealRegister.of(Registers.GR2))
                ));
            }
            case MOD -> {
                tirInstructions.addAll(List.of(
                        new Lad(RealRegister.of(Registers.GR1), convertOperand(binaryOp.left)),
                        new Lad(RealRegister.of(Registers.GR2), convertOperand(binaryOp.right)),
                        new enshud.s4.compiler.generator.impls.casl.tir.instructions.Call("DIV"),
                        new Lad(convertOperand(binaryOp.left), RealRegister.of(Registers.GR1))
                ));
            }
            case AND -> {
                tirInstructions.add(
                        new And(convertOperand(binaryOp.left), convertOperand(binaryOp.right))
                );
            }
            case OR -> { // boolean OR
                tirInstructions.add(
                        new Or(convertOperand(binaryOp.left), convertOperand(binaryOp.right))
                );
            }
            case XOR -> {
                tirInstructions.add(
                        new Xor(convertOperand(binaryOp.left), convertOperand(binaryOp.right))
                );
            }
            case NEQ -> {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Cp(convertOperand(binaryOp.left), convertOperand(binaryOp.right)),
                        new Jmp(labelFalse, Jmp.Type.ZERO),
                        new Lad(convertOperand(binaryOp.left), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(binaryOp.left), Immediate.FALSE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case EQ ->  {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Cp(convertOperand(binaryOp.left), convertOperand(binaryOp.right)),
                        new Jmp(labelFalse, Jmp.Type.NONZERO),
                        new Lad(convertOperand(binaryOp.left), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(binaryOp.left), Immediate.FALSE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case LTE ->   {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(convertOperand(binaryOp.left), convertOperand(binaryOp.right)),
                        new Jmp(labelFalse, Jmp.Type.PLUS),
                        new Lad(convertOperand(binaryOp.left), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(binaryOp.left), Immediate.FALSE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case LT ->    {
                var labelTrue = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(convertOperand(binaryOp.left), convertOperand(binaryOp.right)),
                        new Jmp(labelTrue, Jmp.Type.MINUS),
                        new Lad(convertOperand(binaryOp.left), Immediate.FALSE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(binaryOp.left), Immediate.TRUE).setLabel(labelTrue),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case GTE ->   {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(convertOperand(binaryOp.left), convertOperand(binaryOp.right)),
                        new Jmp(labelFalse, Jmp.Type.MINUS),
                        new Lad(convertOperand(binaryOp.left), Immediate.TRUE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(binaryOp.left), Immediate.FALSE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            case GT ->    {
                var labelFalse = serialManager.New();
                var labelEnd = serialManager.New();
                tirInstructions.addAll(List.of(
                        new Sub(convertOperand(binaryOp.left), convertOperand(binaryOp.right)),
                        new Jmp(labelFalse, Jmp.Type.PLUS),
                        new Lad(convertOperand(binaryOp.left), Immediate.FALSE),
                        new Jmp(labelEnd, Jmp.Type.UNCONDITIONAL),
                        new Lad(convertOperand(binaryOp.left), Immediate.TRUE).setLabel(labelFalse),
                        new Nop().setLabel(labelEnd)
                ));
            }
            default -> throw new UnsupportedOperationException("Unsupported binary operation: " + binaryOp.opType);
        }
        return null;
    }

    @Override
    public Void visit(Load load) {
        return null;
    }

    @Override
    public Void visit(Store store) {
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
                    yield new StringImmediate(String.valueOf(ch));
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
