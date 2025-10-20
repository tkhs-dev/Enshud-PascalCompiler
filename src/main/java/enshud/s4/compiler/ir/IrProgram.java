package enshud.s4.compiler.ir;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class IrProgram {
    private List<Ir> functions;

    public IrProgram() {
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
    public static IrProgram merge(IrProgram... programs) {
        return IrProgram.merge(Arrays.stream(programs));
    }

    public static IrProgram merge(List<? extends IrProgram> programs) {
        IrProgram mergedProgram = new IrProgram();
        for (IrProgram program : programs) {
            mergedProgram.addAll(program.getFunctions());
        }
        return mergedProgram;
    }

    public static IrProgram merge(Stream<? extends IrProgram> programs) {
        IrProgram mergedProgram = new IrProgram();
        programs.forEach(program -> mergedProgram.addAll(program.getFunctions()));
        return mergedProgram;
    }

    public static IrProgram empty() {
        return new IrProgram();
    }

}
