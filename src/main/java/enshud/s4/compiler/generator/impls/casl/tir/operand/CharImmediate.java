package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class CharImmediate extends Immediate<Character> {
    public CharImmediate(Character value) {
        super(value);
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }
}
