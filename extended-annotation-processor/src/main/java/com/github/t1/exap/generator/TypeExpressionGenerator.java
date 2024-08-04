package com.github.t1.exap.generator;

import com.github.t1.exap.insight.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class TypeExpressionGenerator {
    private final TypeGenerator container;
    private final String type;
    private List<Object> args;

    public TypeExpressionGenerator(TypeGenerator container, Type type) {
        this(container, type.getRelativeName());
        container.addImport(type);
    }

    public TypeExpressionGenerator(TypeGenerator container, String type) {
        this.container = container;
        this.type = type;
    }

    public TypeExpressionGenerator typeVar(String arg) {
        if (!container.getTypeParameters().contains(arg))
            throw new IllegalArgumentException("unknown type var [" + arg + "]. " + container.getTypeName()
                                               + " only knows " + container.getTypeParameters());
        if (args == null)
            args = new ArrayList<>();
        args.add(arg);
        return this;
    }

    public TypeExpressionGenerator withTypeArg(Type typeArg) {
        typeArg(typeArg);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public TypeExpressionGenerator typeArg(Type typeArg) {
        container.addImport(typeArg);
        TypeExpressionGenerator sub = new TypeExpressionGenerator(container, typeArg);
        if (args == null)
            args = new ArrayList<>();
        args.add(sub);
        return sub;
    }

    public boolean isVoid() {
        return "void".equals(type);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(type);
        if (args != null) {
            StringJoiner joiner = new StringJoiner(", ", "<", ">");
            for (Object arg : args)
                joiner.add(arg.toString());
            out.append(joiner);
        }
        return out.toString();
    }
}
