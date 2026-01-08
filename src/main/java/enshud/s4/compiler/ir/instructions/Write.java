package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;
import java.util.Optional;

public class Write extends ITInstruction {
    public final Operand operand;
    public final WriteType writeType;

    public Write(Operand operand, WriteType writeType) {
        this.operand = operand;
        this.writeType = writeType;
    }

    public Write(){
        this.operand = null;
        this.writeType = WriteType.NEWLINE;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (writeType == WriteType.NEWLINE) {
            return "Write: NEWLINE";
        } else {
            return String.format("Write: %s (%s)", operand, writeType);
        }
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

    public enum WriteType {
        INT,
        CHAR,
        STRING,
        NEWLINE
    }
}
