package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class VirtualRegister extends TIROperand {
    public final String id;
    public VirtualRegister(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
