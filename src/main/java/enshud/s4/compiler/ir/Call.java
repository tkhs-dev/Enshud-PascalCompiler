package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public class Call extends Ir {

    public final String functionLabel;
    public final List<Temp> args;

    public Call(String functionLabel, List<Temp> args) {
        this.functionLabel = functionLabel;
        this.args = args;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
