package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.DataOperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Ld extends Instr2Op<Ld, VirtualRegister, DataOperand> {
    public Ld(VirtualRegister dest, DataOperand src) {
        super(dest, src);
    }

    public VirtualRegister getDest() {
        return op1.get();
    }

    public DataOperand getSrc() {
        return op2.get();
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    protected Set<OperandRef<?>> getUsedRefs() {
        return Set.of(op2);
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
        var set = new HashSet<VirtualRegister>();
        if (op1.get() instanceof VirtualRegister destReg &&
                op2.get() instanceof VirtualRegister srcReg) {
            set.add(destReg);
            set.add(srcReg);
        }
        return set;
    }
}
