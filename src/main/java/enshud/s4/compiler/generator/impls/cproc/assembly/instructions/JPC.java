package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand;

public class JPC extends JumpInstr<JPC> {
    public JPC(LabelOperand target) {
        super(target);
    }

    @Override
    public boolean isConditionalBranch() {
        return true;
    }
}
