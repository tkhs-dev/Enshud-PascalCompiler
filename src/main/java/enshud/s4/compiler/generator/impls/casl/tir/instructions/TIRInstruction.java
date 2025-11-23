package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import java.util.Optional;

public abstract class TIRInstruction<T extends TIRInstruction<T>> {
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
}
