package enshud.s4.compiler.generator;

import java.util.Set;

public interface UseDef{
    Set<AllocatableRegister> getUse();
    Set<AllocatableRegister> getDef();
    void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg);
    void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg);
}
