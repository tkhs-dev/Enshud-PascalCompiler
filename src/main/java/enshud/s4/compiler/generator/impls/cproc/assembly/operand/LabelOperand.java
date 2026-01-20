package enshud.s4.compiler.generator.impls.cproc.assembly.operand;

public class LabelOperand extends AssemblyOperand {
    private final String label;

    public LabelOperand(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
