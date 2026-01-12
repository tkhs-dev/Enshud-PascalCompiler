package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.Optional;

public class BinaryOp extends IRInstruction {
    public enum OpType {
        ADD, SUB, MUL, DIV, MOD,
        AND, OR,
        EQ, NEQ, LT, LTE, GT, GTE
    }

    public final OpType opType;
    public final OperandRef<Temp> result;
    public final OperandRef<IROperand> left;
    public final OperandRef<IROperand> right;

    public BinaryOp(OpType opType, Temp result, IROperand left, IROperand right) {
        this.opType = opType;
        this.result = new OperandRef<>(result);
        this.left = new OperandRef<>(left);
        this.right = new OperandRef<>(right);
    }

    public IROperand getRight() {
        return right.get();
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
        return String.format("BinaryOp: %s, %s, %s, %s", opType, result, left, right);
    }
}
