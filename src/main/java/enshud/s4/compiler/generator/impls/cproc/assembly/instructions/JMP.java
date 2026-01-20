package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

public class JMP extends JumpInstr<JMP> {
    public JMP(String target) {
        super(target);
    }

    @Override
    public boolean isConditionalBranch() {
        return false;
    }
}
