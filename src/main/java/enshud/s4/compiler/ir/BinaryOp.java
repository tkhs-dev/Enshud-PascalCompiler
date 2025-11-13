package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Symbol;

import java.util.ArrayList;
import java.util.List;

public class BinaryOp extends Ir {
    public enum OpType {
        ADD, SUB, MUL, DIV, MOD,
        AND, OR, XOR,
        EQ, NEQ, LT, LTE, GT, GTE
    }

    public final OpType opType;
    public final Symbol left;
    public final Operand right;

    public BinaryOp(OpType opType, Symbol left, Operand right) {
        this.opType = opType;
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<? extends Symbol> getUsedSymbols() {
        var temps =  new ArrayList<Symbol>();
        temps.add(left);
        if (right instanceof Symbol symbolRight) {
            temps.add(symbolRight);
        }
        return temps;
    }

    @Override
    public String toString() {
        return String.format("BinaryOp: %s, %s, %s", opType, left, right);
    }
}
