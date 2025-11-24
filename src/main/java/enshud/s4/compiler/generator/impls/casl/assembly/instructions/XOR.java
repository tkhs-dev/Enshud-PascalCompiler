package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.DataOperand;
import enshud.s4.compiler.generator.impls.casl.assembly.operand.RegisterOperand;

public class XOR extends OperationInstr {
    public XOR(RegisterOperand registerOperand, DataOperand dataOperand) {
        super(registerOperand, dataOperand);
    }
}
