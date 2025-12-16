package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.Instruction;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public abstract class ITInstruction extends Instruction {
    public abstract <T> T accept(IIrVisitor<T> visitor);
}
