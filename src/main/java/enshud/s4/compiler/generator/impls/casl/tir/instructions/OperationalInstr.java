package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.DataOperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Optional;
import java.util.Set;

public abstract class OperationalInstr<T extends TIrInstr<T>> extends Instr2Op<T, VirtualRegister, DataOperand> {
    public OperationalInstr(VirtualRegister left, DataOperand right) {
        super(left, right);
    }

    public VirtualRegister getLeft() {
        return op1.get();
    }

    public DataOperand getRight() {
        return op2.get();
    }

    @Override
    protected Set<OperandRef<?>> getUsedRefs() {
        return Set.of(op1, op2);
    }

    @Override
    protected Set<OperandRef<?>> getDefinedRefs() {
        return Set.of(op1);
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

    @Override
    public Set<VirtualRegister> getCopiedRegisters() {
        return Set.of();
    }
}
