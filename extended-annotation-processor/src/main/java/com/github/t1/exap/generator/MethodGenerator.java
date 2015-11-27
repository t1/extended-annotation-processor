package com.github.t1.exap.generator;

import static com.github.t1.exap.generator.TypeKind.*;

import java.io.PrintWriter;
import java.util.*;

import com.github.t1.exap.reflection.Type;

public class MethodGenerator {
    private final TypeGenerator container;
    private JavaDocGenerator javaDoc;
    private List<AnnotationGenerator> annotations;
    private boolean isStatic;
    private TypeExpressionGenerator returnType;
    private final String name;
    private List<ParameterGenerator> parameters;
    private String body;

    public MethodGenerator(TypeGenerator container, String name) {
        this.container = container;
        this.name = name;
    }

    public void javaDoc(String javaDoc) {
        if (javaDoc != null && !javaDoc.isEmpty())
            this.javaDoc = new JavaDocGenerator("    ", javaDoc);
    }

    public MethodGenerator setStatic() {
        this.isStatic = true;
        return this;
    }

    public AnnotationGenerator annotation(Type type) {
        if (annotations == null)
            annotations = new ArrayList<>();
        AnnotationGenerator annotationGenerator = new AnnotationGenerator(container, type);
        annotations.add(annotationGenerator);
        return annotationGenerator;
    }

    public TypeExpressionGenerator returnType(String returnType) {
        this.returnType = new TypeExpressionGenerator(container, returnType);
        return this.returnType;
    }

    public TypeExpressionGenerator returnType(Type returnType) {
        this.returnType = new TypeExpressionGenerator(container, returnType);
        return this.returnType;
    }

    public ParameterGenerator addParameter(String name) {
        if (parameters == null)
            parameters = new ArrayList<>();
        ParameterGenerator parameter = new ParameterGenerator(container, name);
        parameters.add(parameter);
        return parameter;
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
        if (javaDoc != null)
            javaDoc.print(out);
        printAnnotations(out);
        out.print("    public ");
        if (isStatic)
            out.print("static ");
        out.print(returnType + " " + name + "(");
        ParameterGenerator.print(parameters, out);
        out.print(")");
        if (body == null)
            out.append(";\n");
        else
            out.append(" {\n").append("        ").append(body).append("\n    }\n");
        out.println();
    }


    private void printAnnotations(PrintWriter out) {
        if (annotations == null)
            return;
        for (AnnotationGenerator annotation : annotations)
            out.println("    " + annotation);
    }
}
