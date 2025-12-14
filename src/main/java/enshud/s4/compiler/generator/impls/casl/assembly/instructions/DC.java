package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import java.util.List;

public class DC extends AssemblyInstr<DC> {
    private final Object value;

    public DC(String value) {
        this.value = value;
    }

    public DC(int value) {
        this.value = value;
    }

    private String formatValue() {
        if (value instanceof String) {
            return String.format("'%s'", value);
        } else {
            return value.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), formatValue() );
    }
}
