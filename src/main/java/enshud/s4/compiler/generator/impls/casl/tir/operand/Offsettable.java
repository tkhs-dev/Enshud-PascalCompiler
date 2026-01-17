package enshud.s4.compiler.generator.impls.casl.tir.operand;

import java.util.Optional;

public interface Offsettable {
    Optional<VirtualRegister> getOffset();
    void setOffset(VirtualRegister offsetRegister);
}
