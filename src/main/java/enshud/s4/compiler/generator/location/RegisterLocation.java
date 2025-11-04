package enshud.s4.compiler.generator.location;

public class RegisterLocation extends Location {
    public final String registerName;

    public RegisterLocation(String registerName) {
        this.registerName = registerName;
    }

    @Override
    public String toString() {
        return "Register(" + registerName + ")";
    }
}
