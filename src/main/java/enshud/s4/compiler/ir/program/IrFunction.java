package enshud.s4.compiler.ir.program;

import enshud.pascal.type.Type;
import enshud.s4.compiler.ir.instructions.ITInstruction;

import java.util.List;
import java.util.Map;

public class IrFunction {
    private final List<IrSlot> functions;
    public final Map<String, Type> variables;
    public final Map<String, Type> parameters;
    public final String name;
    public final int level;
    public final IrFunction parent;

    public IrFunction(String name, int level, IrFunction parent) {
        this.functions = new java.util.ArrayList<>();
        this.variables = new java.util.HashMap<>();
        this.parameters = new java.util.HashMap<>();
        this.name = name;
        this.level = level;
        this.parent = parent;
    }

    public void add(ITInstruction function) {
        this.functions.add(new IrSlot(function));
    }

    public void addAt(int index, ITInstruction function) {
        this.functions.add(index, new IrSlot(function));
    }

    public void addAll(List<? extends ITInstruction> functions) {
        this.functions.addAll(functions.stream().map(IrSlot::new).toList());
    }

    public void addVariable(String name, Type type) {
        this.variables.put(name, type);
    }

    public void addParameter(String name, Type type) {
        this.parameters.put(name, type);
    }

    public List<ITInstruction> getInstructions() {
        return functions.stream().map(slot -> slot.instruction).toList();
    }

    public static IrFunction empty(String name, int level, IrFunction parent) {
        return new IrFunction(name, level, parent);
    }

    public IrEditor editor() {
        return new IrEditor(functions);
    }

    public static class IrSlot {
        public ITInstruction instruction;

        public IrSlot(ITInstruction instruction) {
            this.instruction = instruction;
        }
    }
}
