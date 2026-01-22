package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;


import enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand;

public class JMP extends JumpInstr<JMP> {
    public JMP(LabelOperand target) {
        super(target);
    }

    @Override
    public boolean isConditionalBranch() {
        return false;
    }
}
