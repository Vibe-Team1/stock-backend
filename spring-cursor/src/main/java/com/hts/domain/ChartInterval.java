package com.hts.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ChartInterval {
    ONE_MINUTE("1m"),
    FIVE_MINUTES("5m"),
    TEN_MINUTES("10m");

    private final String value;
    private static final Map<String, ChartInterval> LOOKUP = Arrays.stream(values())
            .collect(Collectors.toMap(ChartInterval::getValue, Function.identity()));

    ChartInterval(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ChartInterval fromString(String value) {
        return LOOKUP.get(value);
    }

    public static boolean isValid(String value) {
        return LOOKUP.containsKey(value);
    }

    public static String[] getValidValues() {
        return Arrays.stream(values())
                .map(ChartInterval::getValue)
                .toArray(String[]::new);
    }
} 