package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;

class ReflectionAnnotationType extends AnnotationType {

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
    public int hashCode() {
        return annotation.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ReflectionAnnotationType that = (ReflectionAnnotationType) obj;
        return this.annotation.equals(that.annotation);
    }

    @Override
    public String toString() {
        return "ReflectionAnnotationType:" + annotation.getName();
    }
}
