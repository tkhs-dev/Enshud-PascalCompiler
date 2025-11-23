package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.location.RegisterLocation;

public enum Registers {
    GR0("GR0"),
    GR1("GR1"),
    GR2("GR2"),
    GR3("GR3"),
    GR4("GR4"),
    GR5("GR5"),
    GR6("GR6"),
    GR7("GR7"),
    GR8("GR8");

    public final String name;

    public static final Registers TEMP = GR0;
    public static final Registers OFFSET = GR2;
    public static final Registers FP = GR7;
    public static final Registers SP = GR8;

    public static final Registers[] GENERAL_PURPOSE_REGISTERS = {
            GR1, GR3, GR4, GR5, GR6
    };

    Registers(String name) {
        this.name = name;
    }

    public RegisterLocation asLocation() {
        return new RegisterLocation(name);
    }
}
