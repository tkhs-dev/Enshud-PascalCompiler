package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;

import java.util.Optional;

public class Jump extends IRInstruction {
    public final String target;
    public final OperandRef<IROperand> condition; // 条件付きジャンプの場合の条件オペランド
    public final Type type;

    public Jump(String target, IROperand condition, Type type) {
        this.target = target;
        this.condition = new OperandRef<>(condition);
        this.type = type;
    }

    public Jump(String target) {
        this(target, null, Type.UNCONDITIONAL);
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isBranch() {
        return true;
    }

    @Override
    public boolean isConditionalBranch() {
        return type != Type.UNCONDITIONAL;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.of(target);
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("Jump: %s %s %s", type, condition.get() != null ? condition.get().toString() : "", target);
    }

    public enum Type {
        UNCONDITIONAL,
        TRUE,
        FALSE
    }
}
