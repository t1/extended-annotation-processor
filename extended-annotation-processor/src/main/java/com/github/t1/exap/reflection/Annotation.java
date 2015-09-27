package com.github.t1.exap.reflection;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;

public class Annotation {
    public static Annotation of(AnnotationMirror mirror, ProcessingEnvironment env) {
        return new Annotation(mirror, env);
    }

    private final AnnotationMirror mirror;
    private final ProcessingEnvironment env;

    public Annotation(AnnotationMirror mirror, ProcessingEnvironment env) {
        this.mirror = mirror;
        this.env = env;
    }

    public Type getAnnotationType() {
        return Type.of(mirror.getAnnotationType(), env);
    }

    public Map<String, Object> getElementValues() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet())
            result.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        return result;
    }
}
