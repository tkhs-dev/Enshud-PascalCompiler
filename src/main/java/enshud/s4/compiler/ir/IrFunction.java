package enshud.s4.compiler.ir;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class IrFunction {
    private List<Ir> functions;

    public IrFunction() {
        this.functions = new java.util.ArrayList<>();
    }

    public void add(Ir function) {
        this.functions.add(function);
    }

    public void addAll(List<? extends Ir> functions) {
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
    public static IrFunction merge(IrFunction... programs) {
        return IrFunction.merge(Arrays.stream(programs));
    }

    public static IrFunction merge(List<? extends IrFunction> programs) {
        IrFunction mergedProgram = new IrFunction();
        for (IrFunction program : programs) {
            mergedProgram.addAll(program.getFunctions());
        }
        return mergedProgram;
    }

    public static IrFunction merge(Stream<? extends IrFunction> programs) {
        IrFunction mergedProgram = new IrFunction();
        programs.forEach(program -> mergedProgram.addAll(program.getFunctions()));
        return mergedProgram;
    }

    public static IrFunction empty() {
        return new IrFunction();
    }

}
