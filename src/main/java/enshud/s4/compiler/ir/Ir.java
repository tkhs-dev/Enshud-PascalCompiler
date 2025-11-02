package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public abstract class Ir {
    public abstract <T> T accept(IIrVisitor<T> visitor);
    public abstract List<Temp> getUsedTemps();
}
