package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.DataOperand;
import enshud.s4.compiler.generator.impls.casl.assembly.operand.RegisterOperand;

public abstract class OperationInstr extends Instr2Op<RegisterOperand, DataOperand> {
    public OperationInstr(RegisterOperand registerOperand, DataOperand dataOperand) {
        super(registerOperand, dataOperand);
    }
}
