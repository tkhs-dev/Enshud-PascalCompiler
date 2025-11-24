package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.MemoryOperand;
import enshud.s4.compiler.generator.impls.casl.assembly.operand.RegisterOperand;

public class ST extends Instr2Op<RegisterOperand, MemoryOperand> {
    public ST(RegisterOperand registerOperand, MemoryOperand memoryOperand) {
        super(registerOperand, memoryOperand);
    }
}
