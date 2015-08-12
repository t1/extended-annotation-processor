package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;

import javax.lang.model.element.TypeElement;

public class AnnotationType {
    private final TypeElement annotation;

    public AnnotationType(TypeElement annotation) {
        this.annotation = annotation;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return annotation.getAnnotation(type);
    }

    @Override
    public String toString() {
        return "AnnotationType:" + annotation.getQualifiedName();
    }
}
