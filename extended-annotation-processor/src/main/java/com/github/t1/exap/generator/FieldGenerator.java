package com.github.t1.exap.generator;

import com.github.t1.exap.insight.Type;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FieldGenerator {
    final TypeGenerator container;
    private final String name;
    private boolean isFinal;
    private TypeExpressionGenerator type;

    public FieldGenerator(TypeGenerator container, String name) {
        this.container = container;
        this.name = name;
    }

    @Override public String toString() {
        var out = new StringWriter();
        print(new PrintWriter(out));
        return out.toString();
    }

    public FieldGenerator setFinal() {
        this.isFinal = true;
        return this;
    }

    public TypeExpressionGenerator type(Type type) {
        container.addImport(type);
        this.type = new TypeExpressionGenerator(container, type);
        return this.type;
    }

    public void typeArg(Type type) {
        container.addImport(type);
    }


    public TypeExpressionGenerator getType() {
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
