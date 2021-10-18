package com.booster.command.arguments;

import java.util.Arrays;

public interface Args {

    static String[] splitAndStrip(String flagsAndValues) {
        return Arrays.stream(flagsAndValues.split("="))
                .map(String::strip)
                .toArray(String[]::new);
    }

}
