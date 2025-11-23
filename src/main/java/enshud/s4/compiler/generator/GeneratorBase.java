package enshud.s4.compiler.generator;

import enshud.s4.compiler.ir.program.IrProgram;

public abstract class GeneratorBase<T> {
    public abstract T generate(IrProgram program);
}
