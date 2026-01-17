package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.AnalysableInstruction;
import enshud.s4.compiler.generator.impls.casl.tir.operand.*;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public abstract class TIrInstr<T extends TIrInstr<T>> extends AnalysableInstruction<VirtualRegister> {
    public abstract <R> R accept(ITIRVisitor<R> visitor);

    public T withLabel(String label) {
        setLabel(label);
        return (T) this;
    }

    public abstract Set<? extends TIROperand> getOperands();

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

    protected String formatInstructionWithOpCode(String opCode, Object... operands) {
        var instr = opCode;
        if (operands.length != 0) {
            instr = String.format("%s %s", opCode, String.join(", ",
                    java.util.Arrays.stream(operands)
                            .map(Object::toString)
                            .toArray(String[]::new)
            ));
        }
        if(label != null && !label.isBlank()) {
            return String.format("%s: %s", label, instr);
        } else {
            return instr;
        }
    }

    protected String formatInstruction(Object... operands) {
        return formatInstructionWithOpCode(this.getClass().getSimpleName().toUpperCase(), operands);
    }

    @Override
    public String toString() {
        return formatInstructionWithOpCode(this.getClass().getSimpleName().toUpperCase());
    }

    protected Set<VirtualRegister> registerSet(Set<OperandRef<?>> ops) {
        var allocatableReg =  ops.stream()
                .map(OperandRef::get)
                .filter(op -> op instanceof VirtualRegister)
                .map(op -> (VirtualRegister) op)
                .collect(java.util.stream.Collectors.toSet());
        var fromOffset = ops.stream()
                .map(OperandRef::get)
                .filter(op -> op instanceof AddressBasedOperand)
                .map(op -> (AddressBasedOperand) op)
                .map(slot -> slot.getOffset().orElse(null))
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
        allocatableReg.addAll(fromOffset);
        return allocatableReg;
    }

    protected void replaceVirtualRegister(VirtualRegister oldReg, VirtualRegister newReg, OperandRef... ops) {
        for(var opRef : ops) {
            if(opRef.get().equals(oldReg)) {
                opRef.set(newReg);
            } else if (opRef.get() instanceof AddressBasedOperand addr) {
                if (addr.getOffset().isPresent() && addr.getOffset().get().equals(oldReg)) {
                    addr.setOffset(newReg);
                }
            }
        }
    }

    public boolean isRegisterUsed(VirtualRegister reg) {
        return this.getUse().contains(reg);
    }

    public boolean isRegisterDefined(VirtualRegister reg) {
        return this.getDef().contains(reg);
    }
}
