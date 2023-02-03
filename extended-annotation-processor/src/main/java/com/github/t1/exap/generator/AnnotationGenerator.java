package com.github.t1.exap.generator;

import com.github.t1.exap.reflection.Type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AnnotationGenerator {
    private final Type annotation;
    private Map<String, Object> properties;

    public AnnotationGenerator(TypeGenerator annotated, Type annotation) {
        this.annotation = annotation;
        annotated.addImport(annotation);
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
