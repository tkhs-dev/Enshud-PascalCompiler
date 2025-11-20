package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public class Jump extends Ir {
    public static final int TYPE_UNCONDITIONAL = 0;
    public static final int TYPE_TRUE = 1;
    public static final int TYPE_FALSE = 2;

    public final String target;
    public final Operand condition; // 条件付きジャンプの場合の条件オペランド
    public final int type;

    public Jump(String target, Operand condition, int type) {
        this.target = target;
        this.condition = condition;
        this.type = type;
    }

    public Jump(String target) {
        this(target, null, TYPE_UNCONDITIONAL);
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
}
