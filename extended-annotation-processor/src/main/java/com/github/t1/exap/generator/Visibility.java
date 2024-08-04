package com.github.t1.exap.generator;

public enum Visibility {
    PUBLIC,
    PROTECTED,
    PACKAGE_PRIVATE {
        @Override public String toString() {return "";}
    },
    PRIVATE;

    @Override public String toString() {return name().toLowerCase() + " ";}
}
