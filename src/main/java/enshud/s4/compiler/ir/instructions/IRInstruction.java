package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.instruction.Instruction;
import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.IROperand;

import java.util.List;
import java.util.Set;

public abstract class IRInstruction extends Instruction {
    public abstract <T> T accept(IIrVisitor<T> visitor);

    @SafeVarargs
    protected final <T extends IROperand> void replaceOperand(
            IROperand oldOperand,
            IROperand newOperand,
            Class<T> clazz,
            OperandRef<T>...ops
    ) {
        for (var opRef : ops) {
            if (opRef.get() == oldOperand) {
                if(!clazz.isInstance(newOperand)) {
                    throw new IllegalArgumentException("newOperand is not of the expected type");
                }
                opRef.set(clazz.cast(newOperand));
            }
        }
    }

    @SafeVarargs
    protected final Set<IROperand> operandSet(OperandRef<? extends IROperand>... ops) {
        var set = new java.util.HashSet<IROperand>();
        for (var opRef : ops) {
            set.add(opRef.get());
        }
        return set;
    }

    protected final Set<IROperand> operandSet(List<OperandRef<IROperand>> ops) {
        var set = new java.util.HashSet<IROperand>();
        for (var opRef : ops) {
            set.add(opRef.get());
        }
        return set;
    }
}
