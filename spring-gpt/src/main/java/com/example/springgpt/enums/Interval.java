package com.example.springgpt.enums;

import java.util.Arrays;

public enum Interval {
    ONE_MIN("1m"), FIVE_MIN("5m"), TEN_MIN("10m");

    private final String label;

    Interval(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static boolean isValid(String input) {
        return Arrays.stream(Interval.values())
                     .anyMatch(i -> i.label.equalsIgnoreCase(input));
    }
}
