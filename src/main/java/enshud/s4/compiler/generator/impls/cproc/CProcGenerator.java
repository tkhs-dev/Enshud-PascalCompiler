package enshud.s4.compiler.generator.impls.cproc;

import enshud.s4.compiler.generator.GeneratorBase;
import enshud.s4.compiler.generator.Register;
import enshud.s4.compiler.generator.impls.cproc.assembly.instructions.*;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.AssemblyOperand;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.ImmediateOperand;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;
import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.instructions.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.IROperand;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;
import enshud.s4.compiler.ir.program.IrProgram;
import enshud.util.SerialManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CProcGenerator extends GeneratorBase<String> {
    @Override
    public String generate(IrProgram program) {
        var tirInstructions = new IrConverter().convert(program.main.getInstructions());
        var assemblyInstructions = new TIrConverter().convert(tirInstructions);
        StringBuilder sb = new StringBuilder();
        for (var instr : assemblyInstructions) {
            sb.append(instr.toString()).append("\n");
        }
        return sb.toString();
    }
}
