package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Temp;

public class GetArgument extends Ir {
    public final Temp arg;

    public GetArgument(Temp arg) {
        this.arg = arg;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "GetArgument";
    }
}
