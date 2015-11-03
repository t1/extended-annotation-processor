package com.github.t1.exap.generator;

import java.io.PrintWriter;

import com.github.t1.exap.reflection.Type;

public class FieldGenerator {
    final TypeGenerator container;
    private final String name;
    private boolean isFinal;
    private TypeStringGenerator type;

    public FieldGenerator(TypeGenerator container, String name) {
        this.container = container;
        this.name = name;
    }

    public FieldGenerator setFinal() {
        this.isFinal = true;
        return this;
    }

    public TypeStringGenerator type(Type type) {
        container.addImport(type);
        this.type = new TypeStringGenerator(container, type);
        return this.type;
    }

    public void typeArg(Type type) {
        container.addImport(type);
    }


    public TypeStringGenerator getType() {
        return type;
    }


    public void print(PrintWriter out) {
        out.print("    private ");
        if (isFinal)
            out.print("final ");
        out.print(type);
        out.print(" ");
        out.print(name);
        out.println(";");
    }
}
