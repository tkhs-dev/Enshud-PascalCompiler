package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public class Call extends ITInstruction {

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Call: ").append(functionLabel);
        if (!args.isEmpty()) {
            sb.append(" ");
            for (int i = 0; i < args.size(); i++) {
                sb.append(args.get(i).toString());
                if (i < args.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }
}
