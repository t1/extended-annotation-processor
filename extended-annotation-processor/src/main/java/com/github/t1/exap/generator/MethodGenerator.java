package com.github.t1.exap.generator;

import static com.github.t1.exap.generator.TypeKind.*;

import java.io.PrintWriter;
import java.util.*;

import com.github.t1.exap.reflection.Type;

public class MethodGenerator {
    private final TypeGenerator container;
    private final String name;
    private TypeExpressionGenerator returnType;
    private String body;
    private boolean isStatic;
    private List<String> annotations = new ArrayList<>();

    public MethodGenerator(TypeGenerator container, String name) {
        this.container = container;
        this.name = name;
    }

    public MethodGenerator setStatic() {
        this.isStatic = true;
        return this;
    }

    // TODO return a new class AnnotationExpressionGenerator
    public void annotation(Type type) {
        this.annotations.add(type.getSimpleName());
        container.addImport(type);
    }

    public TypeExpressionGenerator returnType(String returnType) {
        this.returnType = new TypeExpressionGenerator(container, returnType);
        return this.returnType;
    }

    public TypeExpressionGenerator returnType(Type returnType) {
        this.returnType = new TypeExpressionGenerator(container, returnType);
        return this.returnType;
    }

    public MethodGenerator body(String body) {
        if (container.kind() == CLASS)
            this.body = body;
        else
            throw new IllegalStateException("can't add method body to an " + container.kind() + " method");
        return this;
    }


    public boolean isStatic() {
        return isStatic;
    }

    public void print(PrintWriter out) {
        for (String annotation : annotations) {
            out.print("    @");
            out.println(annotation);
        }
        out.print("    public ");
        if (isStatic)
            out.print("static ");
        out.print(returnType + " " + name + "()");
        if (body == null)
            out.append(";\n");
        else
            out.append(" {\n").append("        ").append(body).append("\n    }\n");
        out.println();
    }
}
