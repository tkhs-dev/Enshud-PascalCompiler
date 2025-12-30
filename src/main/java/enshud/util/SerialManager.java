package enshud.util;

public class SerialManager {
    private int counter;
    private final String prefix;

    public SerialManager(String prefix, int initialValue) {
        this.counter = initialValue;
        this.prefix = prefix;
    }

    public SerialManager(String prefix) {
        this(prefix, 0);
    }

    public String New() {
        counter++;
        return prefix + counter;
    }

    public void reset() {
        counter = 0;
    }

    public void setCounter(int value) {
        this.counter = value;
    }
}
