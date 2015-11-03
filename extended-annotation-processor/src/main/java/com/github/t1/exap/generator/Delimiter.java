package com.github.t1.exap.generator;

public class Delimiter {
    private final String delimiter;
    private boolean first = true;

    public Delimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String next() {
        if (first) {
            first = false;
            return "";
        } else {
            return delimiter;
        }
    }
}
