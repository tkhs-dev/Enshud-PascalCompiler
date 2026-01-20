package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.ImmediateOperand;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Optional;

public class LDI extends AssemblyInstr<LDI> {
    private final OperandRef<RegisterOperand> dest;
    private final OperandRef<ImmediateOperand> value;

    public LDI(RegisterOperand dest, ImmediateOperand value) {
        this.dest = new OperandRef<>(dest);
        this.value = new OperandRef<>(value);
    }

    public RegisterOperand getDest() {
        return dest.get();
    }

    public ImmediateOperand getValue() {
        return value.get();
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
}
