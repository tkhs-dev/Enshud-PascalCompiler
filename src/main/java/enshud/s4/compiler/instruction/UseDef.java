package enshud.s4.compiler.instruction;

import java.util.Set;

public interface UseDef<T>{
    Set<T> getUse();
    Set<T> getDef();
    void replaceUse(T oldUse, T newUse);
    void replaceDef(T oldDef, T newDef);
}
