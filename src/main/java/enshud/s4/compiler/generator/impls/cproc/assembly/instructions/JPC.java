package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

public class JPC extends JumpInstr<JPC> {
    public JPC(String target) {
        super(target);
    }

    @Override
    public boolean isConditionalBranch() {
        return true;
    }
}
