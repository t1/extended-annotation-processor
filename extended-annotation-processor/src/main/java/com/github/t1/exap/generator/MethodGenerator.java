package com.github.t1.exap.generator;

import java.io.PrintWriter;

public class MethodGenerator {
    private final TypeGenerator container;
    private final String name;
    private TypeStringGenerator returnType;
    private String body;
    private boolean isStatic;

    public MethodGenerator(TypeGenerator container, String name) {
        this.container = container;
        this.name = name;
    }

    public MethodGenerator setStatic() {
        this.isStatic = true;
        return this;
    }

    public TypeStringGenerator returnType(String returnType) {
        this.returnType = new TypeStringGenerator(container, returnType);
        return this.returnType;
    }

    public MethodGenerator body(String body) {
        this.body = body;
        return this;
    }


    public boolean isStatic() {
        return isStatic;
    }

    public void print(PrintWriter out) {
        out.print("    public ");
        if (isStatic)
            out.print("static ");
        out.println(returnType + " " + name + "() {");
        out.println("        " + body);
        out.println("    }");
        out.println();
    }
}
