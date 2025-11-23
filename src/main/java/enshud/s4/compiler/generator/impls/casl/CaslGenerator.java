package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.GeneratorBase;
import enshud.s4.compiler.ir.program.IrProgram;

public class CaslGenerator extends GeneratorBase<String> {
    @Override
    public String generate(IrProgram irProgram) {
        Ir2TIr converter = new Ir2TIr();
        var program = converter.convert(irProgram);
        program.main.getInstructions().forEach(System.out::println);
        program.functions.values().forEach(func -> {
            func.getInstructions().forEach(System.out::println);
        });
        return "";
    }
}