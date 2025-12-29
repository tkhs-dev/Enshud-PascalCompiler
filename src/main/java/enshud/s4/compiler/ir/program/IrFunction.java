package enshud.s4.compiler.ir.program;

import enshud.pascal.type.Type;
import enshud.s4.compiler.instruction.FunctionUnit;
import enshud.s4.compiler.instruction.InstructionSet;
import enshud.s4.compiler.ir.instructions.ITInstruction;

import java.util.Map;

public class IrFunction extends FunctionUnit<ITInstruction> {
    public final Map<String, Type> variables;
    public final Map<String, Type> parameters;
    public final String name;
    public String label = null;
    public final int level;
    public final IrFunction parent;

    public IrFunction(String name, InstructionSet<ITInstruction> instructions, int level, IrFunction parent) {
        super(name, instructions);
        this.variables = new java.util.HashMap<>();
        this.parameters = new java.util.HashMap<>();
        this.name = name;
        this.level = level;
        this.parent = parent;
    }

    public void addVariable(String name, Type type) {
        this.variables.put(name, type);
    }

    public void addParameter(String name, Type type) {
        this.parameters.put(name, type);
    }

    @Override
    protected void onChange() {
    }

    public static IrFunction empty(String name, int level, IrFunction parent) {
        return new IrFunction(name, new InstructionSet<>(), level, parent);
    }
}
