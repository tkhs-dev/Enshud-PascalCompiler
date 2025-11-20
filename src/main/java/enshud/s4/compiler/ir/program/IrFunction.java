package enshud.s4.compiler.ir.program;

import enshud.s4.compiler.ir.instructions.Ir;

import java.util.List;

public class IrFunction {
    private final List<IrSlot> functions;
    public final String name;
    public final int level;

    public IrFunction(String name, int level) {
        this.functions = new java.util.ArrayList<>();
        this.name = name;
        this.level = level;
    }

    public void add(Ir function) {
        this.functions.add(new IrSlot(function));
    }

    public void addAt(int index, Ir function) {
        this.functions.add(index, new IrSlot(function));
    }

    public void addAll(List<? extends Ir> functions) {
        this.functions.addAll(functions.stream().map(IrSlot::new).toList());
    }

    public List<Ir> getInstructions() {
        return functions.stream().map(slot -> slot.instruction).toList();
    }

    public static IrFunction empty(String name, int level) {
        return new IrFunction(name, level);
    }

    public IrEditor editor() {
        return new IrEditor(functions);
    }

    public static class IrSlot {
        public Ir instruction;

        public IrSlot(Ir instruction) {
            this.instruction = instruction;
        }
    }
}
