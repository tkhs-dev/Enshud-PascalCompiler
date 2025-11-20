package enshud.s4.compiler.ir.program;

import enshud.s4.compiler.ir.instructions.Ir;

import java.util.List;
import java.util.function.Function;

public class IrEditor {
    List<IrFunction.IrSlot> functions;
    IrEditor(List<IrFunction.IrSlot> functions) {
        this.functions = functions;
    }

    public void apply(int index, Function<Ir, Ir> func) {
        IrFunction.IrSlot slot = functions.get(index);
        slot.instruction = func.apply(slot.instruction);
    }
}