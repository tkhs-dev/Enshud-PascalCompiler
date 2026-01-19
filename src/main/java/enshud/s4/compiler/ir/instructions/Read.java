package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;

import java.util.Optional;

public class Read extends IRInstruction {
    public final OperandRef<IROperand> operand;
    public final ReadType readType;

    public Read(IROperand operand, ReadType readType) {
        this.operand = new OperandRef<>(operand);
        this.readType = readType;
    }

    public Read(){
        this(null, ReadType.NEWLINE);
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
        if (readType == ReadType.NEWLINE) {
            return "Read: NEWLINE";
        } else {
            return String.format("Read: %s (%s)", operand, readType);
        }
    }

    public enum ReadType {
        INT,
        CHAR,
        STRING,
        NEWLINE
    }
}
