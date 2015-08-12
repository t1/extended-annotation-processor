package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;

public class ReflectionParameter extends Parameter {
    private final java.lang.reflect.Parameter parameter;

    public ReflectionParameter(java.lang.reflect.Parameter parameter) {
        super(null);
        this.parameter = parameter;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return parameter.getAnnotation(type);
    }

    @Override
    public String toString() {
        return "ReflectionParameter:" + parameter;
    }
}
