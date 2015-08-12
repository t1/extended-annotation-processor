package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;

public class ReflectionAnnotationType extends AnnotationType {

    private final Class<? extends Annotation> annotation;

    public ReflectionAnnotationType(Class<? extends Annotation> annotation) {
        super(null);
        this.annotation = annotation;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return annotation.getAnnotation(type);
    }

    @Override
    public String toString() {
        return "ReflectionAnnotationType:" + annotation.getName();
    }
}
