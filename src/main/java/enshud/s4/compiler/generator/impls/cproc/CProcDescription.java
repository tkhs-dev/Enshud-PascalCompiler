package enshud.s4.compiler.generator.impls.cproc;

import enshud.s4.compiler.generator.Register;
import enshud.s4.compiler.generator.allocate.TargetDescription;

import java.util.List;

import static enshud.s4.compiler.generator.impls.cproc.CProcRegisters.RA;
import static enshud.s4.compiler.generator.impls.cproc.CProcRegisters.RB;

public class CProcDescription implements TargetDescription {
    private CProcDescription() {
    }

    @Override
    public List<Register> getAllRegisters() {
        return List.of(RA, RB);
    }

    @Override
    public List<Register> getAllocatableRegisters() {
        return List.of(RA, RB);
    }

    @Override
    public List<Register> getCallerSaved() {
        return List.of();
    }

    @Override
    public List<Register> getCalleeSaved() {
        return List.of();
    }

    @Override
    public List<Register> getReservedRegisters() {
        return List.of();
    }

    @Override
    public List<Register> getScratchCandidates() {
        return List.of(RA, RB);
    }

    public static CProcDescription getInstance() {
        return new CProcDescription();
    }
}
