package enshud.s4.compiler.generator;

import enshud.s4.compiler.instruction.Instruction;
import enshud.s4.compiler.instruction.UseDef;

import java.util.Set;

public abstract class AnalysableInstruction extends Instruction implements UseDef<AllocatableRegister> {
    public boolean isCall(){
        return false;
    }
    public abstract Set<AllocatableRegister> getCopiedRegisters();
}
