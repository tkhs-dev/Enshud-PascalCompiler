package enshud.s4.compiler.ir;

import java.util.List;

public class IrFunction {
    private List<Ir> functions;
    public final String name;
    public final int level;

    public IrFunction(String name, int level) {
        this.functions = new java.util.ArrayList<>();
        this.name = name;
        this.level = level;
    }

    public void add(Ir function) {
        this.functions.add(function);
    }

    public void addAll(List<? extends Ir> functions) {
        this.functions.addAll(functions);
    }

    public List<Ir> getInstructions() {
        return functions;
    }

    public void visitAll(IIrVisitor<Void> visitor) {
        for (Ir function : functions) {
            function.accept(visitor);
        }
    }

    public static IrFunction empty(String name, int level) {
        return new IrFunction(name, level);
    }
}
