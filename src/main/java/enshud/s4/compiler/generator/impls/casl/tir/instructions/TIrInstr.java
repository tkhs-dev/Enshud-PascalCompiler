package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.AnalysableInstruction;
import enshud.s4.compiler.generator.impls.casl.tir.operand.Address;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public abstract class TIrInstr<T extends TIrInstr<T>> extends AnalysableInstruction {
    private String label = null;

    public abstract <R> R accept(ITIRVisitor<R> visitor);

    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    public T setLabel(String label) {
        this.label = label;
        return (T) this;
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

    protected Set<AllocatableRegister> registerSet(TIROperand... ops) {
        var allocatableReg =  Arrays.stream(ops)
                .filter(op -> op instanceof AllocatableRegister)
                .map(op -> (AllocatableRegister) op)
                .collect(java.util.stream.Collectors.toSet());
        var fromOffset = Arrays.stream(ops)
                .filter(op -> op instanceof Address)
                .map(op -> (Address) op)
                .map(addr -> addr.offset)
                .filter(offset -> offset instanceof AllocatableRegister)
                .map(offset -> (AllocatableRegister) offset)
                .collect(java.util.stream.Collectors.toSet());
        allocatableReg.addAll(fromOffset);
        return allocatableReg;
    }

    protected void replaceAllocatable(TIROperand target, AllocatableRegister oldOp, AllocatableRegister newOp) {
        if (target instanceof AllocatableRegister ar && ar.equals(oldOp)) {
            ar.replaceRegister(newOp);
        } else if (target instanceof Address addr) {
            if (addr.offset instanceof AllocatableRegister offsetReg && offsetReg.equals(oldOp)) {
                offsetReg.replaceRegister(newOp);
            }
        }
    }
}
