package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.AnalysableInstruction;
import enshud.s4.compiler.generator.impls.casl.tir.operand.Address;
import enshud.s4.compiler.generator.impls.casl.tir.operand.FrameSlot;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Arrays;
import java.util.Set;

public abstract class TIrInstr<T extends TIrInstr<T>> extends AnalysableInstruction {
    public abstract <R> R accept(ITIRVisitor<R> visitor);

    public T withLabel(String label) {
        setLabel(label);
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

    protected Set<AllocatableRegister> registerSet(OperandRef... ops) {
        var allocatableReg =  Arrays.stream(ops)
                .map(opRef -> opRef.operand)
                .filter(op -> op instanceof AllocatableRegister)
                .map(op -> (AllocatableRegister) op)
                .collect(java.util.stream.Collectors.toSet());
        var fromAddressOffset = Arrays.stream(ops)
                .map(opRef -> opRef.operand)
                .filter(op -> op instanceof Address)
                .map(op -> (Address) op)
                .map(addr -> addr.offset)
                .filter(offset -> offset instanceof AllocatableRegister)
                .map(offset -> (AllocatableRegister) offset)
                .collect(java.util.stream.Collectors.toSet());
        var fromFrameSlotOffset = Arrays.stream(ops)
                .map(opRef -> opRef.operand)
                .filter(op -> op instanceof FrameSlot)
                .map(op -> (FrameSlot) op)
                .map(slot -> slot.offset)
                .filter(offset -> offset instanceof AllocatableRegister)
                .map(offset -> (AllocatableRegister) offset)
                .collect(java.util.stream.Collectors.toSet());
        allocatableReg.addAll(fromAddressOffset);
        allocatableReg.addAll(fromFrameSlotOffset);
        return allocatableReg;
    }

    protected void replaceAllocatable(AllocatableRegister oldReg, AllocatableRegister newReg, OperandRef... ops) {
        if (oldReg instanceof TIROperand oldOp && newReg instanceof TIROperand newOp) {
            replaceOperand(oldOp, newOp, ops);
            return;
        }
        throw new IllegalArgumentException("Both oldReg and newReg must be TIROperand instances.");
    }

    protected void replaceOperand(TIROperand oldOp, TIROperand newOp, OperandRef... ops) {
        for (OperandRef opRef : ops) {
            if (opRef.operand.equals(oldOp)) {
                opRef.operand = newOp;
            } else if (opRef.operand instanceof Address addr) {
                if (addr.offset.equals(oldOp)) {
                    opRef.operand = new Address(addr.base).setOffset(newOp);
                }
            } else if(opRef.operand instanceof FrameSlot slot) {
                if (slot.offset.equals(oldOp)) {
                    FrameSlot newSlot = new FrameSlot(slot.slot, slot.levelDiff);
                    newSlot.setOffset(newOp);
                    opRef.operand = newSlot;
                }
            }
        }
    }

    protected static class OperandRef {
        public TIROperand operand;

        public OperandRef(TIROperand operand) {
            this.operand = operand;
        }

        @Override
        public String toString() {
            return operand.toString();
        }
    }
}
