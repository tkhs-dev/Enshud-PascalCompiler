package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

public abstract class JmpInstr extends AssemblyInstr<JmpInstr> {
    protected String operand;

    public JmpInstr(String operand) {
        this.operand = operand;
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), operand);
    }
}
