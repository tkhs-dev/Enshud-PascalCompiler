package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.DataOperand;
import enshud.s4.compiler.generator.impls.casl.assembly.operand.RegisterOperand;

public class SRA extends OperationInstr {
    public SRA(RegisterOperand registerOperand, DataOperand dataOperand) {
        super(registerOperand, dataOperand);
    }
}
