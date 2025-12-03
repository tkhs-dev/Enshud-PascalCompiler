package enshud.s4.compiler.generator;

import java.util.Set;

public interface UseDef{
    Set<AllocatableRegister> getUse();
    Set<AllocatableRegister> getDef();
}
