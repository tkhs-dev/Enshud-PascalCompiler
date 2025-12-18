package enshud.s4.compiler.generator;

import java.util.Optional;

public interface AllocatableRegister {
    public Optional<Register> getPredefined();
    public void replaceRegister(AllocatableRegister newReg);
}
