package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

public class UnaryOp extends ITInstruction {
    public enum OpType {
        NEG, NOT
    }

    public final OpType opType;
    public final Operand operand;
    public final Temp result;

    public UnaryOp(OpType opType, Temp result, Operand operand) {
        this.opType = opType;
        this.result = result;
        this.operand = operand;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("UnaryOp: %s, %s, %s", opType, result, operand);
    }
}