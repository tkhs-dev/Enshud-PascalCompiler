package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public class Label extends Ir {
    public final String name;

    public Label(String name) {
        this.name = name;
    }

    public static Label of(String name) {
        return new Label(name);
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<Temp> getUsedTemps() {
        return List.of();
    }

    @Override
    public String toString() {
        return String.format("%s:", name);
    }
}
