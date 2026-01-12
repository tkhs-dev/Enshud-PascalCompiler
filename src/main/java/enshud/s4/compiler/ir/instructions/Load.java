package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;

import java.util.Optional;

public class Load extends IRInstruction {
    public final OperandRef<Temp> targetVar;
    public final OperandRef<Variable> var;

    public Load(Temp targetVar, Variable var) {
        this.targetVar = new OperandRef<>(targetVar);
        this.var = new OperandRef<>(var);
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
        return String.format("Load: %s <- %s", targetVar.get().id, var.get().name);
    }
}
