package enshud.s4.compiler.instruction;

public class OperandRef<T extends Operand> {
    private T operand;

    public OperandRef(T operand) {
        this.operand = operand;
    }

    public T get() {
        return operand;
    }

    public void set(T operand) {
        this.operand = operand;
    }

    @Override
    public String toString() {
        return operand.toString();
    }
}
