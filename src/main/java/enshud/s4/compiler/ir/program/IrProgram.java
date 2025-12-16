package enshud.s4.compiler.ir.program;

import enshud.s4.compiler.ir.instructions.ITInstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IrProgram {
    public final IrFunction main;

    public final Map<String, IrFunction> functions;

    public IrProgram(IrFunction main, Map<String, IrFunction> functions) {
        this.main = main;
        this.functions = functions;
    }

    public List<ITInstruction> getInstructions() {
        List<ITInstruction> instructions = new ArrayList<>();
        instructions.addAll(main.getInstructions());
        for(IrFunction f : functions.values()) {
            instructions.addAll(f.getInstructions());
        }
        return instructions;
    }
}
