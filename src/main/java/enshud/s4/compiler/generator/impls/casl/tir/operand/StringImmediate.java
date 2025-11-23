package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class StringImmediate extends Immediate<String> {
    public StringImmediate(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }
}
