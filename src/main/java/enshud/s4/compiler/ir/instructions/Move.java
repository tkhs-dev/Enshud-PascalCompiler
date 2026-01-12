package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.Optional;

public class Move extends IRInstruction {
    public final OperandRef<Temp> dest;
    public final OperandRef<IROperand> src;

    public Move(Temp dest, IROperand src) {
        this.dest = new OperandRef<>(dest);
        this.src = new OperandRef<>(src);
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
        return String.format("Move: %s <- %s", dest, src);
    }
}
