package enshud.s4.compiler.ir.program;

import enshud.s4.compiler.instruction.InstructionSet;
import enshud.s4.compiler.ir.instructions.ITInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IrProgram {
    public final IrFunction main;

    public final Map<String, IrFunction> functions;

    public IrProgram(IrFunction main, Map<String, IrFunction> functions) {
        this.main = main;
        this.functions = functions;
    }

    public InstructionSet<ITInstruction> getInstructions() {
        List<ITInstruction> instructions = new ArrayList<>();
        instructions.addAll(main.getInstructions().toList());
        for(IrFunction f : functions.values()) {
            instructions.addAll(f.getInstructions().toList());
        }
        return InstructionSet.of(instructions);
    }
}
