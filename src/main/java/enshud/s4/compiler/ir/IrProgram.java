package enshud.s4.compiler.ir;

import java.util.List;

public class IrProgram {
    private List<Ir> functions;

    public IrProgram() {
        this.functions = List.of();
    }

    public void add(Ir function) {
        this.functions.add(function);
    }

    public void addAll(List<Ir> functions) {
        this.functions.addAll(functions);
    }

    public List<Ir> getFunctions() {
        return functions;
    }

    public void visitAll(IIrVisitor<Void> visitor) {
        for (Ir function : functions) {
            function.accept(visitor);
        }
    }

    // 与えられた順番にマージする
    public static IrProgram merge(IrProgram... programs) {
        IrProgram mergedProgram = new IrProgram();
        for (IrProgram program : programs) {
            mergedProgram.addAll(program.getFunctions());
        }
        return mergedProgram;
    }

}
