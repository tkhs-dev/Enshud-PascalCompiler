package enshud.s4.compiler.generator.impls.cproc.tir.program;

import enshud.s4.compiler.generator.impls.cproc.tir.instructions.TIRInstr;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.LabelOperand;
import enshud.s4.compiler.instruction.FunctionUnit;
import enshud.s4.compiler.instruction.InstructionSet;

import java.util.List;

public class TIrFunction extends FunctionUnit<TIRInstr<?>> {
    private final List<LabelOperand> variables;

    public TIrFunction(String name, InstructionSet<TIRInstr<?>> instructions, List<LabelOperand> variables) {
        super(name, instructions);
        this.variables = variables;
    }

    public List<LabelOperand> getVariables() {
        return variables;
    }
}
