package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.AnalysableInstruction;
import enshud.s4.compiler.generator.InterferenceGraph;
import enshud.s4.compiler.instruction.FunctionUnit;
import enshud.s4.compiler.instruction.Instruction;

import java.util.Objects;

public abstract class Allocator {
    protected FunctionUnit<? extends Instruction> function;
    protected TargetDescription target;

    public Allocator(FunctionUnit<? extends AnalysableInstruction> function, TargetDescription targetDescription) {
        this.function = Objects.requireNonNull(function);
        this.target = Objects.requireNonNull(targetDescription);
    }

    public abstract Allocation allocate();
}
