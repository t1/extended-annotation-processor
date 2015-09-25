package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.lang.model.element.VariableElement;

public class ReflectionParameter extends Parameter implements ReflectionMessageTarget {
    private final java.lang.reflect.Parameter parameter;

    public ReflectionParameter(Method method, java.lang.reflect.Parameter parameter) {
        super(method, DummyProxy.of(VariableElement.class));
        this.parameter = parameter;
    }

    @Override
    public String getName() {
        return parameter.getName();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return parameter.getAnnotation(type);
    }

    @Override
    public List<AnnotationType> getAnnotationTypes() {
        List<AnnotationType> result = new ArrayList<>();
        for (Annotation annotation : parameter.getAnnotations())
            result.add(new ReflectionAnnotationType(annotation.annotationType()));
        return result;
    }

    @Override
    public Type getType() {
        return new ReflectionType(env(), parameter.getType());
    }

    @Override
    public String toString() {
        return "ReflectionParameter:" + parameter;
    }
}
