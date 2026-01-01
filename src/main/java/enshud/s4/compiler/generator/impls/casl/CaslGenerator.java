package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.GeneratorBase;
import enshud.s4.compiler.generator.impls.casl.tir.TIrOptimizer;
import enshud.s4.compiler.ir.program.IrProgram;

public class CaslGenerator extends GeneratorBase<String> {
    @Override
    public String generate(IrProgram irProgram) {
        Ir2TIr converter = new Ir2TIr();
        var program = converter.convert(irProgram);
        new TIrOptimizer().optimize(program);
        program.main.getInstructions().forEach(System.out::println);
        program.functions.values().forEach(func -> {
            func.getInstructions().forEach(System.out::println);
        });

        var assemblyGenerator = new AssemblyGenerator(program);
        var instructions = assemblyGenerator.generate();
        StringBuilder sb = new StringBuilder();
        for (var instr : instructions) {
            sb.append(instr.toString()).append("\n");
        }

        return sb.toString();
    }
}