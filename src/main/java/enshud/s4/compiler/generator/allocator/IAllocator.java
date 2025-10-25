package enshud.s4.compiler.generator.allocator;

import enshud.s4.compiler.ir.IrProgram;

public interface IAllocator {
    Allocation allocateVariables(IrProgram program);
}
