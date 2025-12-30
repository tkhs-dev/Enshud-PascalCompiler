package enshud.util;

public class SerialManager {
    private int counter;
    private final String prefix;

    public SerialManager(String prefix) {
        this.counter = 0;
        this.prefix = prefix;
    }

    public String New() {
        counter++;
        return prefix + counter;
    }

    public void reset() {
        counter = 0;
    }
}
