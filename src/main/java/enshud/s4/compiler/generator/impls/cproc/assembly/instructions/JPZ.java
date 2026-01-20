package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

public class JPZ extends JumpInstr<JPZ> {
    public JPZ(String target) {
        super(target);
    }

    @Override
    public boolean isConditionalBranch() {
        return true;
    }
}
