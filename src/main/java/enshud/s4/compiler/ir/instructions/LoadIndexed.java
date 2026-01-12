package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;

import java.util.Optional;

public class LoadIndexed extends IRInstruction {
    public final OperandRef<Temp> targetVar;
    public final OperandRef<Variable> arrayVar;
    public final OperandRef<IROperand> indexVar;

    public LoadIndexed(Temp targetVar, Variable arrayVar, IROperand indexVar) {
        this.targetVar = new OperandRef<>(targetVar);
        this.arrayVar = new OperandRef<>(arrayVar);
        this.indexVar = new OperandRef<>(indexVar);
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
        return String.format("Load: %s <- %s[%s]", targetVar, arrayVar.get().name, indexVar);
    }
}
