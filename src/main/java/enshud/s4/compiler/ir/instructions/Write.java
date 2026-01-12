package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;

import java.util.Optional;

public class Write extends IRInstruction {
    public final OperandRef<IROperand> operand;
    public final WriteType writeType;

    public Write(IROperand operand, WriteType writeType) {
        this.operand = new OperandRef<>(operand);
        this.writeType = writeType;
    }

    public Write(){
        this(null, WriteType.NEWLINE);
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isBranch() {
        return false;
    }

    @Override
    public boolean isConditionalBranch() {
        return false;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.empty();
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public String toString() {
        if (writeType == WriteType.NEWLINE) {
            return "Write: NEWLINE";
        } else {
            return String.format("Write: %s (%s)", operand, writeType);
        }
    }

    public enum WriteType {
        INT,
        CHAR,
        STRING,
        NEWLINE
    }
}
