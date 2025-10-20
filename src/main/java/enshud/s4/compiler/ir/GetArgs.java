package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public class GetArgs extends Ir {
    public final List<Temp> temps;

    public GetArgs(List<Temp> temps) {
        this.temps = temps;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GetArgs ");
        for (int i = 0; i < temps.size(); i++) {
            sb.append(temps.get(i).toString());
            if (i != temps.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
