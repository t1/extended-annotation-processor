package com.github.t1.exap.generator;

import java.util.*;
import java.util.Map.Entry;

import com.github.t1.exap.reflection.Type;

public class AnnotationGenerator {
    private final TypeGenerator annotated;
    private final Type annotation;
    private Map<String, Object> properties;

    public AnnotationGenerator(TypeGenerator annotated, Type annotation) {
        this.annotated = annotated;
        this.annotation = annotation;

        this.annotated.addImport(annotation);
    }

    public void set(String property, String value) {
        put(property, value);
    }

    private void put(String property, Object value) {
        if (properties == null)
            properties = new LinkedHashMap<>();
        properties.put(property, value);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("@").append(annotation.getSimpleName());
        if (properties != null && !properties.isEmpty()) {
            out.append('(');
            for (Entry<String, Object> entry : properties.entrySet()) {
                out.append('"').append(entry.getValue()).append('"');
            }
            out.append(')');
        }
        return out.toString();
    }
}
