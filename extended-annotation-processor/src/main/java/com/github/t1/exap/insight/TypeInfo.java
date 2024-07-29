package com.github.t1.exap.insight;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;

public class TypeInfo {
    private final Class<?> type;
    private StringBuilder out;
    private String indent = "    ";

    public TypeInfo(Class<?> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        if (out == null) {
            out = new StringBuilder();
            buildTypeInfo(type);
        }
        return out.toString();
    }

    private void buildTypeInfo(java.lang.reflect.Type t) {
        indent();
        if (t instanceof Class) {
            Class<?> c = (Class<?>) t;
            out.append(c.getName());
            if (c.getSuperclass() != null)
                buildTypeInfo(c.getSuperclass());
            for (java.lang.reflect.Type i : c.getInterfaces())
                buildTypeInfo(i);
        } else if (t instanceof TypeVariable) {
            TypeVariable<?> v = (TypeVariable<?>) t;
            out.append(v.getName()).append(": ");
            for (java.lang.reflect.Type bound : v.getBounds())
                buildTypeInfo(bound);
        } else if (t instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) t;
            for (java.lang.reflect.Type arg : p.getActualTypeArguments())
                buildTypeInfo(arg);
        } else {
            out.append("[[").append(t).append(":").append(t.getClass()).append("]]");
        }
        outdent();
    }

    private void indent() {
        out.append("\n").append(indent);
        indent = indent + "    ";
    }

    private void outdent() {
        indent = indent.substring(0, indent.length() - 4);
    }
}
