package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand;

public class JPZ extends JumpInstr<JPZ> {
    public JPZ(LabelOperand target) {
        super(target);
    }

    @Override
    public boolean isConditionalBranch() {
        return true;
    }
}
