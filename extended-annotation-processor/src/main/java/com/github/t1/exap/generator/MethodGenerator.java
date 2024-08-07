package com.github.t1.exap.generator;

import com.github.t1.exap.insight.Type;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static com.github.t1.exap.generator.TypeKind.CLASS;
import static java.lang.String.join;

@SuppressWarnings("UnusedReturnValue")
public class MethodGenerator {
    private final TypeGenerator container;
    private JavaDocGenerator javaDoc;
    private List<AnnotationGenerator> annotations;
    private boolean isStatic;
    private TypeExpressionGenerator returnType;
    private final String name;
    private List<ParameterGenerator> parameters;
    private List<String> throwsList;
    private String body;
    private Visibility visibility;

    public MethodGenerator(TypeGenerator container, Visibility visibility, String name) {
        this.container = container;
        this.name = name;
        this.returnType = new TypeExpressionGenerator(container, "void");
        this.visibility = visibility;
    }

    @Override public String toString() {
        var out = new StringWriter();
        print(new PrintWriter(out));
        return out.toString();
    }

    public void javaDoc(String javaDoc) {
        if (javaDoc != null && !javaDoc.isEmpty())
            this.javaDoc = new JavaDocGenerator("    ", javaDoc);
    }

    public MethodGenerator setStatic() {
        this.isStatic = true;
        return this;
    }

    public MethodGenerator visibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    public AnnotationGenerator annotation(Type type) {
        if (annotations == null)
            annotations = new ArrayList<>();
        var annotationGenerator = new AnnotationGenerator(container, type);
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
        var parameter = new ParameterGenerator(container, name);
        parameters.add(parameter);
        return parameter;
    }

    public MethodGenerator addThrows(Type type) {
        container.addImport(type);
        if (throwsList == null) throwsList = new ArrayList<>();
        throwsList.add(type.getSimpleName());
        return this;
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
        out.println();
        if (javaDoc != null)
            javaDoc.print(out);
        printAnnotations(out);
        out.print("    ");
        out.print(visibility);
        if (isStatic)
            out.print("static ");
        out.print(returnType + " " + name + "(");
        ParameterGenerator.print(parameters, out);
        out.print(")");
        if (throwsList != null)
            out.append(" throws ").append(join(", ", throwsList));
        if (body == null)
            out.append(";\n");
        else
            out.append(" {\n").append("        ").append(body).append("\n    }\n");
    }


    private void printAnnotations(PrintWriter out) {
        if (annotations == null)
            return;
        for (var annotation : annotations)
            out.println("    " + annotation);
    }
}
