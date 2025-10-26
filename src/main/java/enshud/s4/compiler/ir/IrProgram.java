package enshud.s4.compiler.ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IrProgram {
    public final IrFunction main;

    public final Map<String, IrFunction> functions;

    public final IrFunction data;

    public IrProgram() {
        this.main = IrFunction.empty();
        this.functions = new HashMap<>();
        this.data = IrFunction.empty();
    }

    public List<Ir> getInstructions() {
        List<Ir> instructions = new ArrayList<>();
        instructions.addAll(main.getFunctions());
        for(IrFunction f : functions.values()) {
            instructions.addAll(f.getFunctions());
        }
        instructions.addAll(data.getFunctions());
        return instructions;
    }
}
