package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;

import java.util.List;
import java.util.Optional;

public class Call extends IRInstruction {

    public final String functionLabel;
    public final List<OperandRef<IROperand>> args;

    public Call(String functionLabel, List<IROperand> args) {
        this.functionLabel = functionLabel;
        this.args = args.stream().map(OperandRef<IROperand>::new).toList();
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

    @Override
    public boolean isBranch() {
        return false;
    }

    @Override
    public boolean isConditionalBranch() {
        return false;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.empty();
    }

    @Override
    public boolean isTerminal() {
        return false;
    }
}
