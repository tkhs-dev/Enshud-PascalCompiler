package enshud.s4.compiler.generator;

import java.util.Set;

public interface UseDef<T>{
    Set<T> getUse();
    Set<T> getDef();
}
