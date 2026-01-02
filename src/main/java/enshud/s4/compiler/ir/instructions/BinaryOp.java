package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.ArrayList;
import java.util.List;

public class BinaryOp extends ITInstruction {
    public enum OpType {
        ADD, SUB, MUL, DIV, MOD,
        AND, OR, XOR,
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
