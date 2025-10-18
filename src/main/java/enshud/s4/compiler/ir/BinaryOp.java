package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;

public class BinaryOp extends Ir {
    public enum OpType {
        ADD, SUB, MUL, DIV,
        AND, OR,
        EQ, NEQ, LT, LEQ, GT, GEQ
    }

    public final OpType opType;
    public final Operand left;
    public final Operand right;

    public BinaryOp(OpType opType, Operand left, Operand right) {
        this.opType = opType;
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
