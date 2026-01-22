package enshud.s4.compiler.generator.impls.cproc.tir.instructions;

import enshud.s4.compiler.generator.AnalysableInstruction;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Set;

public abstract class TIRInstr<T extends TIRInstr<?>> extends AnalysableInstruction<VirtualRegister> {

    public T withLabel(String label) {
        this.setLabel(label);
        return (T) this;
    }

    public abstract <R> R accept(ITIRVisitor<R> visitor);

    protected abstract Set<OperandRef<?>> getUsedRefs();

    protected abstract Set<OperandRef<?>> getDefinedRefs();

    @Override
    public Set<VirtualRegister> getUse() {
        return registerSet(getUsedRefs());
    }

    @Override
    public Set<VirtualRegister> getDef() {
        return registerSet(getDefinedRefs());
    }

    @Override
    public void replaceUse(VirtualRegister oldUse, VirtualRegister newUse) {
        replaceVirtualRegister(oldUse, newUse, getUsedRefs().toArray(new OperandRef<?>[0]));
    }

    @Override
    public void replaceDef(VirtualRegister oldDef, VirtualRegister newDef) {
        replaceVirtualRegister(oldDef, newDef, getDefinedRefs().toArray(new OperandRef<?>[0]));
    }

    private Set<VirtualRegister> registerSet(Set<OperandRef<?>> refs) {
        return refs.stream()
                .filter(ref -> ref.get() instanceof VirtualRegister)
                .map(ref -> (VirtualRegister) ref.get())
                .collect(java.util.stream.Collectors.toSet());
    }

    private void replaceVirtualRegister(VirtualRegister oldReg, VirtualRegister newReg, OperandRef... refs) {
        for (var ref : refs) {
            if (ref.get() instanceof VirtualRegister vr && vr.equals(oldReg)) {
                ref.set(newReg);
            }
        }
    }

    @Override
    public String toString() {
        if (getLabel().isEmpty()) {
            return String.format("\t%s", this.getClass().getSimpleName());
        } else {
            return String.format("%s:\t%s", getLabel().get(), this.getClass().getSimpleName());
        }
    }
}
