package enshud.s4.compiler.generator.impls.cproc;

import enshud.s4.compiler.generator.GeneratorBase;
import enshud.s4.compiler.generator.Register;
import enshud.s4.compiler.generator.impls.cproc.assembly.instructions.*;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.AssemblyOperand;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.ImmediateOperand;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.instructions.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.IROperand;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;
import enshud.s4.compiler.ir.program.IrProgram;
import enshud.util.SerialManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CProcGenerator extends GeneratorBase<String> {
    @Override
    public String generate(IrProgram program) {
        return "";
    }
}

class IrConverter implements IIrVisitor<Void> {

    List<AssemblyInstr<?>> instructions = new ArrayList<>();

    Map<String, LabelOperand> variableMap;

    SerialManager variableSerialManager = new SerialManager("V");

    @Override
    public Void visit(Call ir) {
        throw new UnsupportedOperationException("Function call is not supported.");
    }

    @Override
    public Void visit(Jump ir) {
        instructions.add(
                new JMP(ir.target)
        );
        return null;
    }

    @Override
    public Void visit(Label ir) {
        instructions.add(
                new NOP().withLabel(ir.name)
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
        return null;
    }

    @Override
    public Void visit(BinaryOp ir) {
        var dest = new RegisterOperand(CProcRegisters.RA);
        var tmp = new RegisterOperand(CProcRegisters.RB);
        switch (ir.opType){
            case ADD -> {
                convertOperand(ir.left.get(), CProcRegisters.RA);
                convertOperand(ir.right.get(), CProcRegisters.RB);
                instructions.add(
                        new ADD(dest)
                );
            }
            case SUB -> {
                convertOperand(ir.left.get(), CProcRegisters.RA);
                convertOperand(ir.right.get(), CProcRegisters.RB);
                instructions.add(
                        new SUB(dest)
                );
            }
            case MUL -> {
                convertOperand(ir.left.get(), CProcRegisters.RA);
                convertOperand(ir.right.get(), CProcRegisters.RB);
                instructions.add(
                        new MUL(dest)
                );
            }
            case DIV -> {
                throw new UnsupportedOperationException("Division is not supported.");
            }
            case MOD -> {
                throw new UnsupportedOperationException("Modulo is not supported.");
            }
            case AND -> {
                convertOperand(ir.left.get(), CProcRegisters.RA);
                convertOperand(ir.right.get(), CProcRegisters.RB);
                instructions.add(
                        new AND(dest)
                );
            }
            case OR -> {
                convertOperand(ir.left.get(), CProcRegisters.RA);
                convertOperand(ir.right.get(), CProcRegisters.RB);
                instructions.add(
                        new OR(dest)
                );
            }
            case EQ -> {
                var trueLabel = variableSerialManager.New();
                var endLabel = variableSerialManager.New();
                convertOperand(ir.left.get(), CProcRegisters.RA);
                convertOperand(ir.right.get(), CProcRegisters.RB);
                instructions.addAll(List.of(
                        new CMP(),
                        new JPZ(trueLabel),
                        new LDI(dest, new ImmediateOperand(0)),
                        new JMP(endLabel),
                        new LDI(dest, new ImmediateOperand(1)).withLabel(trueLabel),
                        new NOP().withLabel(endLabel)
                ));
            }
            case NEQ -> {
                var trueLabel = variableSerialManager.New();
                var endLabel = variableSerialManager.New();
                convertOperand(ir.left.get(), CProcRegisters.RA);
                convertOperand(ir.right.get(), CProcRegisters.RB);
                instructions.addAll(List.of(
                        new CMP(),
                        new JPZ(trueLabel),
                        new LDI(dest, new ImmediateOperand(1)),
                        new JMP(endLabel),
                        new LDI(dest, new ImmediateOperand(0)).withLabel(trueLabel),
                        new NOP().withLabel(endLabel)
                ));
            }
            case LT -> { // a < b
                var trueLabel = variableSerialManager.New();
                var endLabel = variableSerialManager.New();
                convertOperand(ir.left.get(), CProcRegisters.RA);
                convertOperand(ir.right.get(), CProcRegisters.RB);
                instructions.addAll(List.of(
                        new SUB(dest),
                        new LDI(tmp, new ImmediateOperand(0b10000000)),
                        new AND(dest),
                        new JPZ(trueLabel),
                        new LDI(dest, new ImmediateOperand(0)),
                        new JMP(endLabel),
                        new LDI(dest, new ImmediateOperand(1)).withLabel(trueLabel),
                        new NOP().withLabel(endLabel)
                ));
            }
            case LTE -> { // a <= b
                var trueLabel = variableSerialManager.New();
                var endLabel = variableSerialManager.New();
                convertOperand(ir.left.get(), CProcRegisters.RB);
                convertOperand(ir.right.get(), CProcRegisters.RA);
                instructions.addAll(List.of(
                        new SUB(dest),
                        new LDI(tmp, new ImmediateOperand(0b10000000)),
                        new AND(dest),
                        new JPZ(trueLabel),
                        new LDI(dest, new ImmediateOperand(1)),
                        new JMP(endLabel),
                        new LDI(dest, new ImmediateOperand(0)).withLabel(trueLabel),
                        new NOP().withLabel(endLabel)
                ));
            }
            case GT -> { // a > b
                var trueLabel = variableSerialManager.New();
                var endLabel = variableSerialManager.New();
                convertOperand(ir.left.get(), CProcRegisters.RB);
                convertOperand(ir.right.get(), CProcRegisters.RA);
                instructions.addAll(List.of(
                        new SUB(dest),
                        new LDI(tmp, new ImmediateOperand(0b10000000)),
                        new AND(dest),
                        new JPZ(trueLabel),
                        new LDI(dest, new ImmediateOperand(0)),
                        new JMP(endLabel),
                        new LDI(dest, new ImmediateOperand(1)).withLabel(trueLabel),
                        new NOP().withLabel(endLabel)
                ));
            }
            case GTE -> { // a >= b
                var trueLabel = variableSerialManager.New();
                var endLabel = variableSerialManager.New();
                convertOperand(ir.left.get(), CProcRegisters.RA);
                convertOperand(ir.right.get(), CProcRegisters.RB);
                instructions.addAll(List.of(
                        new SUB(dest),
                        new LDI(tmp, new ImmediateOperand(0b10000000)),
                        new AND(dest),
                        new JPZ(trueLabel),
                        new LDI(dest, new ImmediateOperand(1)),
                        new JMP(endLabel),
                        new LDI(dest, new ImmediateOperand(0)).withLabel(trueLabel),
                        new NOP().withLabel(endLabel)
                ));
            }
        }
        return null;
    }

    @Override
    public Void visit(UnaryOp ir) {
        var dest = new RegisterOperand(CProcRegisters.RA);
        var tmp = new RegisterOperand(CProcRegisters.RB);
        switch (ir.opType){
            case NEG -> {
                convertOperand(ir.operand.get(), tmp.register);
                instructions.addAll(List.of(
                        new LDI(dest, new ImmediateOperand(0)),
                        new SUB(dest)
                ));
            }
            case NOT -> {
                instructions.add(
                        new NOT(dest)
                );
            }
        }
        return null;
    }

    @Override
    public Void visit(Load ir) {
        return null;
    }

    @Override
    public Void visit(Store ir) {
        return null;
    }

    private AssemblyOperand convertOperand(IROperand operand, Register register) {
        var reg = new RegisterOperand(register);
        switch (operand) {
            case Temp temp -> {
                return reg;
            }
            case Const constOp -> {
                if (constOp.value instanceof Integer intValue) {
                    instructions.add(
                            new LDI(reg, new ImmediateOperand(intValue))
                    );
                    return reg;
                } else {
                    throw new IllegalArgumentException("Only integer constants are supported: " + operand);
                }
            }
            case Variable varOp -> {
                return variableMap.putIfAbsent(varOp.name, new LabelOperand(variableSerialManager.New()));
            }
            default -> throw new IllegalArgumentException("Operand is not a register: " + operand);
        }
    }

    private RegisterOperand register(AssemblyOperand operand) {
        if (operand instanceof RegisterOperand regOp) {
            return regOp;
        } else {
            throw new IllegalArgumentException("Operand is not a register: " + operand);
        }
    }

    private ImmediateOperand immediate(AssemblyOperand operand) {
        if (operand instanceof ImmediateOperand immOp) {
            return immOp;
        } else {
            throw new IllegalArgumentException("Operand is not an immediate: " + operand);
        }
    }

    private LabelOperand label(AssemblyOperand operand) {
        if (operand instanceof LabelOperand labelOp) {
            return labelOp;
        } else {
            throw new IllegalArgumentException("Operand is not a label: " + operand);
        }
    }
}
