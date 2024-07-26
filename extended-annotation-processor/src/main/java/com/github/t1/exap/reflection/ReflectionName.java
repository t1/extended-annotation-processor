package com.github.t1.exap.reflection;

import javax.lang.model.element.Name;

import static java.util.Objects.requireNonNull;

public class ReflectionName implements Name {
    private final String name;

    public ReflectionName(String name) {this.name = requireNonNull(name);}

    @Override public boolean contentEquals(CharSequence cs) {
        return name.contentEquals(cs);
    }

    @Override public int length() {return name.length();}

    @Override public char charAt(int index) {return name.charAt(index);}

    @Override public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }

    @Override public String toString() {return name;}
}
