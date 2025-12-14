package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.Register;

public class CaslRegisters {
    public static final Register GR0 = new Register("GR0");
    public static final Register GR1 = new Register("GR1");
    public static final Register GR2 = new Register("GR2");
    public static final Register GR3 = new Register("GR3");
    public static final Register GR4 = new Register("GR4");
    public static final Register GR5 = new Register("GR5");
    public static final Register GR6 = new Register("GR6");
    public static final Register GR7 = new Register("GR7");
    public static final Register GR8 = new Register("GR8");

    public static final Register TEMP = GR2;
    public static final Register FP = GR5;
    public static final Register SP = GR8;
}
