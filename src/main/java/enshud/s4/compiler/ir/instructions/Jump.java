package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public class Jump extends ITInstruction {
    public final String target;
    public final Operand condition; // 条件付きジャンプの場合の条件オペランド
    public final Type type;

    public Jump(String target, Operand condition, Type type) {
        this.target = target;
        this.condition = condition;
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
    public List<Temp> getUsedTemps() {
        if (condition != null && condition instanceof Temp temp) {
            return List.of(temp);
        }
        return List.of();
    }

    @Override
    public String toString() {
        return String.format("Goto: %s", target);
    }

    public enum Type {
        UNCONDITIONAL,
        TRUE,
        FALSE
    }
}
