package enshud.s4.compiler.generator.allocator;

import enshud.s4.compiler.ir.program.IrProgram;

public interface IAllocator {
    Allocation allocate(IrProgram program);
}
