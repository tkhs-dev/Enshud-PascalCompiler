package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.Register;
import enshud.s4.compiler.generator.allocate.TargetDescription;

import java.util.List;

import static enshud.s4.compiler.generator.impls.casl.CaslRegisters.*;

public class CaslDescription implements TargetDescription {
    @Override
    public List<Register> getAllRegisters() {
        return List.of(GR0,GR1,GR2,GR3,GR4,GR5,GR6,GR7,GR8);
    }

    @Override
    public List<Register> getAllocatableRegisters() {
        return List.of(GR0, GR3, GR4, GR6, GR7);
    }

    @Override
    public List<Register> getCallerSaved() {
        return List.of(GR6, GR7);
    }

    @Override
    public List<Register> getCalleeSaved() {
        return List.of(GR3, GR4);
    }

    @Override
    public List<Register> getReservedRegisters() {
        return List.of(GR1, GR2, GR5, GR8);
    }

    @Override
    public List<Register> getScratchCandidates() {
        return List.of(GR3, GR4, GR6, GR7);
    }
}
