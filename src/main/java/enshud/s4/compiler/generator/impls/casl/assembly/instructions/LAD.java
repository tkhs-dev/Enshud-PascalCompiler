package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.MemoryOperand;
import enshud.s4.compiler.generator.impls.casl.assembly.operand.RegisterOperand;

import java.util.Optional;

public class LAD extends Instr2Op<RegisterOperand, MemoryOperand> {
    public LAD(RegisterOperand registerOperand, MemoryOperand memoryOperand) {
        super(registerOperand, memoryOperand);
    }

    @Override
    public boolean isBranch() {
        return false;
    }

    @Override
    public boolean isConditionalBranch() {
        return false;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.empty();
    }

    @Override
    public boolean isTerminal() {
        return false;
    }
}
