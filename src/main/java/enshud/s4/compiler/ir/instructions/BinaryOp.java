package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BinaryOp extends ITInstruction {
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

    public enum OpType {
        ADD, SUB, MUL, DIV, MOD,
        AND, OR,
        EQ, NEQ, LT, LTE, GT, GTE
    }

    public final OpType opType;
    public final Temp result;
    public final Operand left;
    public final Operand right;

    public BinaryOp(OpType opType, Temp result, Operand left, Operand right) {
        this.opType = opType;
        this.result = result;
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("BinaryOp: %s, %s, %s, %s", opType, result, left, right);
    }
}
