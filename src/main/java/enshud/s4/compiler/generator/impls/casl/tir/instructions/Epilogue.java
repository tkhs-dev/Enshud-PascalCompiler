package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.CaslRegisters;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Optional;
import java.util.Set;

public class Epilogue extends TIrInstr<Epilogue> {
    private final OperandRef<TIROperand> dummyGR6;
    private final OperandRef<TIROperand> dummyGR7;

    public Epilogue() {
        this.dummyGR6 = new OperandRef<>(VirtualRegister.withPredefined("WRTGR6", CaslRegisters.GR6));
        this.dummyGR7 = new OperandRef<>(VirtualRegister.withPredefined("WRTGR7", CaslRegisters.GR7));
    }

    @Override
    public <R> R accept(ITIRVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Set<? extends TIROperand> getOperands() {
        return Set.of(
                dummyGR6.get(),
                dummyGR7.get()
        );
    }

    @Override
    protected Set<OperandRef<?>> getUsedRefs() {
        return Set.of(dummyGR6, dummyGR7);
    }

    @Override
    protected Set<OperandRef<?>> getDefinedRefs() {
        return Set.of();
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
