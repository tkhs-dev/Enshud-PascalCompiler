package enshud.s4.compiler.generator.impls.cproc.tir.operand;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.Register;

import java.util.Optional;

public class VirtualRegister extends TIROperand implements AllocatableRegister {
    @Override
    public Optional<Register> getPredefined() {
        return Optional.empty();
    }

    @Override
    public double getSpillCost() {
        return 0;
    }

    @Override
    public boolean needsGeneralPurpose() {
        return false;
    }
}
