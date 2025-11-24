package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.RegisterOperand;

public class POP extends Instr1Op<RegisterOperand> {
    public POP(RegisterOperand registerOperand) {
        super(registerOperand);
    }
}
