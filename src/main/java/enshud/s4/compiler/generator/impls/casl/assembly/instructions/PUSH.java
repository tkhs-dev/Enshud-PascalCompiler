package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.MemoryOperand;

public class PUSH extends Instr1Op<MemoryOperand> {
    public PUSH(MemoryOperand memoryOperand) {
        super(memoryOperand);
    }
}
