package com.github.t1.exap.reflection;

import java.util.List;

public class TypeParameter {
    private final String name;
    private final List<Type> bounds;

    public TypeParameter(String name, List<Type> bounds) {
        this.name = name;
        this.bounds = bounds;
    }

    public String getName() {
        return name;
    }

    public List<Type> getBounds() {
        return bounds;
    }

    @Override
    public String toString() {
        return "<" + name + ": " + bounds + ">";
    }

    public String getBoundsString() {
        StringBuilder out = new StringBuilder();
        for (Type bound : bounds) {
            if (out.length() > 0)
                out.append(", ");
            out.append(bound.getQualifiedName());
        }
        return out.toString();
    }
}
