package enshud.s4.compiler.generator;

import enshud.s4.compiler.generator.allocator.Allocation;
import enshud.s4.compiler.generator.allocator.IAllocator;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.IrProgram;

import java.util.List;

public abstract class GeneratorBase<T> {
    public T generate(IrProgram program) {
        preprocess(program);
        Allocation varMap = allocate(program);
        IIrVisitor<T> visitor = getConverter(varMap);
        List<T> assemblies = program.getInstructions().stream().map(f -> f.accept(visitor)).toList();
        return concat(assemblies);
    }

    private Allocation allocate(IrProgram program) {
        IAllocator allocator = getAllocator();
        return allocator.allocate(program);
    }

    // IRに対する前処理
    protected void preprocess(IrProgram program) {
        // デフォルトでは何もしない
    }

    protected abstract T concat(List<T> assemblies);

    protected abstract IAllocator getAllocator();

    protected abstract IIrVisitor<T> getConverter(Allocation allocation);
}
