package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;

import java.util.List;

public class SetArg extends Ir {
    public final List<? extends Operand> args;

    public SetArg(List<? extends Operand> args) {
        this.args = args;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SetArg ");
        for (int i = 0; i < args.size(); i++) {
            sb.append(args.get(i).toString());
            if (i != args.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
