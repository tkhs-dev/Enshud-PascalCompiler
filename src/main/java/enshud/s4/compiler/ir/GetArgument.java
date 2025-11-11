package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Symbol;

import java.util.List;

public class GetArgument extends Ir {
    public final Symbol arg;

    public GetArgument(Symbol arg) {
        this.arg = arg;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<Symbol> getUsedTemps() {
        return List.of(arg);
    }

    @Override
    public String toString() {
        return "GetArgument";
    }
}
