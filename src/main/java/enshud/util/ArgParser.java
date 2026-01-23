package enshud.util;

import java.util.HashMap;
import java.util.Map;

public class ArgParser {

    private final Map<String, String> options = new HashMap<>();
    private final Map<String, Boolean> flags = new HashMap<>();

    public ArgParser(String[] args) {
        parse(args);
    }

    private void parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            // --key=value
            if (arg.startsWith("--") && arg.contains("=")) {
                String[] parts = arg.substring(2).split("=", 2);
                options.put(parts[0], parts[1]);
            }
            // --key value
            else if (arg.startsWith("--")) {
                String key = arg.substring(2);
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    options.put(key, args[++i]);
                } else {
                    flags.put(key, true);
                }
            }
            // -k value / -abc (flagまとめ)
            else if (arg.startsWith("-")) {
                String shortOpts = arg.substring(1);
                if (shortOpts.length() > 1) {
                    for (char c : shortOpts.toCharArray()) {
                        flags.put(String.valueOf(c), true);
                    }
                } else {
                    String key = shortOpts;
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        options.put(key, args[++i]);
                    } else {
                        flags.put(key, true);
                    }
                }
            }
        }
    }

    public String getOption(String key) {
        return options.get(key);
    }

    public String getOption(String key, String defaultValue) {
        return options.getOrDefault(key, defaultValue);
    }

    public boolean hasFlag(String key) {
        return flags.getOrDefault(key, false);
    }
}