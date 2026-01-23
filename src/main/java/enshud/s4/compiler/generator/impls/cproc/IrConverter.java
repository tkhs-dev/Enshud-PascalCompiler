package enshud.s4.compiler.generator.impls.cproc;

import enshud.s4.compiler.generator.impls.cproc.tir.instructions.*;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.ImmediateOperand;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.LabelOperand;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.TIROperand;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.VirtualRegister;
import enshud.s4.compiler.generator.impls.cproc.tir.program.TIrFunction;
import enshud.s4.compiler.instruction.InstructionSet;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.instructions.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.IROperand;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;
import enshud.util.SerialManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IrConverter implements IIrVisitor<Void> {
    List<TIRInstr<?>> instructions = new ArrayList<>();

    Map<String, LabelOperand> variableMap = new java.util.HashMap<>();

    SerialManager variableSerialManager = new SerialManager("V");
    SerialManager tmpSerialManager = new SerialManager("");
    SerialManager labelSerialManager = new SerialManager("BL");

    public TIrFunction convert(InstructionSet<IRInstruction> irInstructions) {
        for (var ir : irInstructions) {
            ir.accept(this);
        }
        instructions.add(new End());
        var instr = InstructionSet.of(instructions);
        return new TIrFunction("main", instr, variableMap.values().stream().toList());
    }

    @Override
    public Void visit(Call ir) {
        throw new UnsupportedOperationException("Function call is not supported.");
    }

    @Override
    public Void visit(Jump ir) {
        switch(ir.type){
            case UNCONDITIONAL -> {
                instructions.add(
                        new Jmp(new LabelOperand(ir.target), Jmp.Type.UNCONDITIONAL)
                );
            }
            case TRUE -> {
                instructions.addAll(List.of(
                        new Cmp(
                                register(convertOperand(ir.condition.get())),
                                register(new ImmediateOperand(1))
                        ),
                        new Jmp(new LabelOperand(ir.target), Jmp.Type.ZERO)
                ));
            }
            case FALSE -> {
                instructions.addAll(List.of(
                        new Cmp(
                                register(convertOperand(ir.condition.get())),
                                register(new ImmediateOperand(0))
                        ),
                        new Jmp(new LabelOperand(ir.target), Jmp.Type.ZERO)
                ));
            }
        }
        return null;
    }

    @Override
    public Void visit(Label ir) {
        instructions.add(
                new Nop().withLabel(ir.name)
        );
        return null;
    }

    @Override
    public Void visit(LoadIndexed ir) {
        throw new UnsupportedOperationException("Array type is not supported.");
    }

    @Override
    public Void visit(Move ir) {
        throw new UnsupportedOperationException("Move instruction is not supported.");
    }

    @Override
    public Void visit(StoreIndexed ir) {
        throw new UnsupportedOperationException("Array type is not supported.");
    }

    @Override
    public Void visit(Write ir) {
        return null;
    }

    @Override
    public Void visit(Read ir) {
        switch(ir.readType){
            case INT -> {
                var startLabel = new LabelOperand(labelSerialManager.New());
                var endLabel = new LabelOperand(labelSerialManager.New());
                var operand = register(convertOperand(ir.operand.get()));
                instructions.addAll(List.of(
                        new Ldd(
                                operand,
                                new LabelOperand("0xFFFE")
                        ).withLabel(startLabel.getLabel()),
                        new Dec(operand),
                        new Jmp(endLabel, Jmp.Type.ZERO),
                        new Jmp(startLabel, Jmp.Type.UNCONDITIONAL),
                        new Ldd(
                                operand,
                                new LabelOperand("0xFFFF")
                        ).withLabel(endLabel.getLabel())
                ));
            }
            case NEWLINE -> {
                var startLabel = new LabelOperand(labelSerialManager.New());
                var endLabel = new LabelOperand(labelSerialManager.New());
                var tmpReg = new VirtualRegister(tmpSerialManager.New());
                instructions.addAll(List.of(
                        new Ldd(
                                tmpReg,
                                new LabelOperand("0xFFFE")
                        ).withLabel(startLabel.getLabel()),
                        new Dec(tmpReg),
                        new Jmp(endLabel, Jmp.Type.ZERO),
                        new Jmp(startLabel, Jmp.Type.UNCONDITIONAL),
                        new Nop().withLabel(endLabel.getLabel())
                ));
            }
            default -> throw new IllegalArgumentException("Read type is not supported: " + ir.readType);
        }
        return null;
    }

    @Override
    public Void visit(BinaryOp ir) {
        switch (ir.opType){
            case ADD -> {
                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Add(
                                leftReg,
                                rightReg
                        ),
                        new Mov(
                                resultReg,
                                leftReg
                        )
                ));
            }
            case SUB -> {
                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Sub(
                                leftReg,
                                rightReg
                        ),
                        new Mov(
                                resultReg,
                                leftReg
                        )
                ));
            }
            case MUL -> {
                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Mul(
                                leftReg,
                                rightReg
                        ),
                        new Mov(
                                resultReg,
                                leftReg
                        )
                ));
            }
            case DIV -> {
                throw new UnsupportedOperationException("Division is not supported.");
            }
            case MOD -> {
                throw new UnsupportedOperationException("Modulo is not supported.");
            }
            case AND -> {
                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new And(
                                leftReg,
                                rightReg
                        ),
                        new Mov(
                                resultReg,
                                leftReg
                        )
                ));
            }
            case OR -> {
                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Or(
                                leftReg,
                                rightReg
                        ),
                        new Mov(
                                resultReg,
                                leftReg
                        )
                ));
            }
            case EQ -> {
                var trueLabel = labelSerialManager.New();
                var endLabel = labelSerialManager.New();

                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Cmp(
                                leftReg,
                                rightReg
                        ),
                        new Jmp(new LabelOperand(trueLabel), Jmp.Type.ZERO),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(0)
                        ),
                        new Jmp(new LabelOperand(endLabel), Jmp.Type.UNCONDITIONAL),
                        new Nop().withLabel(trueLabel),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(1)
                        ),
                        new Nop().withLabel(endLabel)
                ));
            }
            case NEQ -> {
                var trueLabel = labelSerialManager.New();
                var endLabel = labelSerialManager.New();

                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Cmp(
                                leftReg,
                                rightReg
                        ),
                        new Jmp(new LabelOperand(trueLabel), Jmp.Type.ZERO),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(1)
                        ),
                        new Jmp(new LabelOperand(endLabel), Jmp.Type.UNCONDITIONAL),
                        new Nop().withLabel(trueLabel),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(0)
                        ),
                        new Nop().withLabel(endLabel)
                ));
            }
            case LT -> { // a < b
                var trueLabel = labelSerialManager.New();
                var endLabel = labelSerialManager.New();
                var tmp = new VirtualRegister(tmpSerialManager.New());

                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Sub(
                                leftReg,
                                rightReg
                        ),
                        new Ldi(tmp, new ImmediateOperand(0b10000000)),
                        new And(
                                leftReg,
                                tmp
                        ),
                        new Jmp(new LabelOperand(trueLabel), Jmp.Type.ZERO),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(1)
                        ),
                        new Jmp(new LabelOperand(endLabel), Jmp.Type.UNCONDITIONAL),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(0)
                        ).withLabel(trueLabel),
                        new Nop().withLabel(endLabel)
                ));
            }
            case LTE -> { // a <= b
                var trueLabel = labelSerialManager.New();
                var endLabel = labelSerialManager.New();
                var tmp = new VirtualRegister(tmpSerialManager.New());

                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Sub(
                                rightReg,
                                leftReg
                        ),
                        new Ldi(tmp, new ImmediateOperand(0b10000000)),
                        new And(
                                rightReg,
                                tmp
                        ),
                        new Jmp(new LabelOperand(trueLabel), Jmp.Type.ZERO),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(0)
                        ),
                        new Jmp(new LabelOperand(endLabel), Jmp.Type.UNCONDITIONAL),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(1)
                        ).withLabel(trueLabel),
                        new Nop().withLabel(endLabel)
                ));
            }
            case GT -> { // a > b
                var trueLabel = labelSerialManager.New();
                var endLabel = labelSerialManager.New();
                var tmp = new VirtualRegister(tmpSerialManager.New());

                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Sub(
                                rightReg,
                                leftReg
                        ),
                        new Ldi(tmp, new ImmediateOperand(0b10000000)),
                        new And(
                                rightReg,
                                tmp
                        ),
                        new Jmp(new LabelOperand(trueLabel), Jmp.Type.ZERO),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(1)
                        ),
                        new Jmp(new LabelOperand(endLabel), Jmp.Type.UNCONDITIONAL),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(0)
                        ).withLabel(trueLabel),
                        new Nop().withLabel(endLabel)
                ));
            }
            case GTE -> { // a >= b
                var trueLabel = labelSerialManager.New();
                var endLabel = labelSerialManager.New();
                var tmp = new VirtualRegister(tmpSerialManager.New());

                var leftReg = register(convertOperand(ir.left.get()));
                var rightReg = register(convertOperand(ir.right.get()));
                var resultReg = register(convertOperand(ir.result.get()));
                instructions.addAll(List.of(
                        new Sub(
                                leftReg,
                                rightReg
                        ),
                        new Ldi(tmp, new ImmediateOperand(0b10000000)),
                        new And(
                                leftReg,
                                tmp
                        ),
                        new Jmp(new LabelOperand(trueLabel), Jmp.Type.ZERO),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(0)
                        ),
                        new Jmp(new LabelOperand(endLabel), Jmp.Type.UNCONDITIONAL),
                        new Ldi(
                                resultReg,
                                new ImmediateOperand(1)
                        ).withLabel(trueLabel),
                        new Nop().withLabel(endLabel)
                ));
            }
        }
        return null;
    }

    @Override
    public Void visit(UnaryOp ir) {
        switch (ir.opType){
            case NEG -> {
                instructions.addAll(List.of(
                        new Ldi(
                                register(convertOperand(ir.result.get())),
                                new ImmediateOperand(0)
                        ),
                        new Sub(
                                register(convertOperand(ir.result.get())),
                                register(convertOperand(ir.operand.get()))
                        )
                ));
            }
            case NOT -> {
                instructions.add(
                        new Not(
                                register(convertOperand(ir.operand.get()))
                        )
                );
            }
        }
        return null;
    }

    @Override
    public Void visit(Load ir) {
        instructions.add(
                new Ldd(
                        register(convertOperand(ir.targetVar.get())),
                        label(convertOperand(ir.var.get()))
                )
        );
        return null;
    }

    @Override
    public Void visit(Store ir) {
        instructions.add(
                new Std(
                        register(convertOperand(ir.value.get())),
                        label(convertOperand(ir.target.get()))
                )
        );
        return null;
    }

    private TIROperand convertOperand(IROperand operand) {
        switch (operand) {
            case Temp temp -> {
                return new VirtualRegister(temp.id);
            }
            case Const constOp -> {
                if (!(constOp.value instanceof Integer)){
                    throw new IllegalArgumentException("Only integer constants are supported: " + constOp.value);
                }
                return new ImmediateOperand((Integer) constOp.value);
            }
            case Variable varOp -> {
                if (!variableMap.containsKey(varOp.name)){
                    variableMap.put(varOp.name, new LabelOperand(variableSerialManager.New()));
                }
                return variableMap.get(varOp.name);
            }
            default -> throw new IllegalArgumentException("Operand is not supported: " + operand);
        }
    }

    private VirtualRegister register(TIROperand operand) {
        if (operand instanceof VirtualRegister regOp) {
            return regOp;
        } else if (operand instanceof ImmediateOperand imm){
            var reg = new VirtualRegister(tmpSerialManager.New());
            instructions.add(new Ldi(reg, imm));
            return reg;
        } else {
            throw new IllegalArgumentException("Operand is not a register: " + operand);
        }
    }

    private ImmediateOperand immediate(TIROperand operand) {
        if (operand instanceof ImmediateOperand immOp) {
            return immOp;
        } else {
            throw new IllegalArgumentException("Operand is not an immediate: " + operand);
        }
    }

    private LabelOperand label(TIROperand operand) {
        if (operand instanceof LabelOperand labelOp) {
            return labelOp;
        } else {
            throw new IllegalArgumentException("Operand is not a label: " + operand);
        }
    }
}
