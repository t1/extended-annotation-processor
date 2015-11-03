package com.github.t1.exap.generator;

import java.util.*;

import com.github.t1.exap.reflection.Type;

public class TypeStringGenerator {
    private final TypeGenerator container;
    private String type;
    private List<Object> args;

    public TypeStringGenerator(TypeGenerator container, Type type) {
        this(container, type.getSimpleName());
    }

    public TypeStringGenerator(TypeGenerator container, String type) {
        this.container = container;
        this.type = type;
    }

    public TypeStringGenerator typeVar(String arg) {
        if (!container.typeParameters.contains(arg))
            throw new IllegalArgumentException("unknown type var [" + arg + "]. " + container.getTypeName()
                    + " only knows " + container.typeParameters);
        if (args == null)
            args = new ArrayList<>();
        args.add(arg);
        return this;
    }

    public TypeStringGenerator typeArg(Type typeArg) {
        container.addImport(typeArg);
        TypeStringGenerator sub = new TypeStringGenerator(container, typeArg);
        if (args == null)
            args = new ArrayList<>();
        args.add(sub);
        return sub;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(type);
        if (args != null) {
            out.append("<");
            Delimiter delimiter = new Delimiter(", ");
            for (Object arg : args) {
                out.append(delimiter.next()).append(arg);
            }
            out.append(">");
        }
        return out.toString();
    }
}
