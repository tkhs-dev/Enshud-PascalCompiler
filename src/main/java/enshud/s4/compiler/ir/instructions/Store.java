package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;
import enshud.s4.compiler.ir.operand.Variable;

import java.util.Optional;

public class Store extends IRInstruction {
    public final OperandRef<Variable> target;
    public final OperandRef<IROperand> value;

    public Store(Variable target, IROperand value) {
        this.target = new OperandRef<>(target);
        this.value = new OperandRef<>(value);
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
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

    @Override
    public String toString() {
        return String.format("Store: %s <- %s", target.get().name, value);
    }
}
