package com.github.t1.exap.generator;

import java.io.PrintWriter;
import java.util.*;

import com.github.t1.exap.reflection.Type;

public class ParameterGenerator {
    public static void print(List<ParameterGenerator> parameters, PrintWriter out) {
        if (parameters != null) {
            StringJoiner joiner = new StringJoiner(", ");
            for (ParameterGenerator param : parameters)
                joiner.add(param.toString());
            out.print(joiner);
        }
    }

    private final TypeGenerator container;
    private List<AnnotationGenerator> annotations;
    private TypeExpressionGenerator type;
    private final String name;

    public ParameterGenerator(TypeGenerator container, String name) {
        this.container = container;
        this.name = name;
    }

    public ParameterGenerator type(TypeExpressionGenerator type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public TypeExpressionGenerator getType() {
        return type;
    }

    public AnnotationGenerator annotation(Type annotation) {
        if (annotations == null)
            annotations = new ArrayList<>();
        AnnotationGenerator annotationGenerator = new AnnotationGenerator(container, annotation);
        annotations.add(annotationGenerator);
        return annotationGenerator;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        if (annotations != null)
            for (AnnotationGenerator annotation : annotations)
                out.append(annotation).append(" ");
        out.append(type).append(" ").append(name);
        return out.toString();
    }

}
