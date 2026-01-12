package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.Optional;

public class UnaryOp extends IRInstruction {
    public enum OpType {
        NEG, NOT
    }

    public final OpType opType;
    public final OperandRef<IROperand> operand;
    public final OperandRef<Temp> result;

    public UnaryOp(OpType opType, Temp result, IROperand operand) {
        this.opType = opType;
        this.result = new OperandRef<>(result);
        this.operand = new OperandRef<>(operand);
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isBranch() {
        return false;
    }

    @Override
    public boolean isConditionalBranch() {
        return false;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.empty();
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("UnaryOp: %s, %s, %s", opType, result, operand);
    }
}