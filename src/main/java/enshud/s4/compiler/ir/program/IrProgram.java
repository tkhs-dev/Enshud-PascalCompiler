package enshud.s4.compiler.ir.program;

import enshud.s4.compiler.ir.instructions.Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IrProgram {
    public final IrFunction main;

    public final Map<String, IrFunction> functions;

    public final IrFunction data;

    public IrProgram() {
        this.main = IrFunction.empty("MAIN", 0);
        this.functions = new HashMap<>();
        this.data = IrFunction.empty("DATA", 0);
    }

    public List<Ir> getInstructions() {
        List<Ir> instructions = new ArrayList<>();
        instructions.addAll(main.getInstructions());
        for(IrFunction f : functions.values()) {
            instructions.addAll(f.getInstructions());
        }
        instructions.addAll(data.getInstructions());
        return instructions;
    }
}
