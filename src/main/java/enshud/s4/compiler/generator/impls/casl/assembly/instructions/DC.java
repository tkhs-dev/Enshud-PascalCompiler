package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import java.util.List;

public class DC extends Instr{
    public final List<Integer> values;

    public DC(List<Integer> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), String.join(", ", values.stream().map(Object::toString).toList()));
    }
}
