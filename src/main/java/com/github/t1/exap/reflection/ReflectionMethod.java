package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.Messager;

public class ReflectionMethod extends Method {

    private final java.lang.reflect.Method method;

    public ReflectionMethod(Messager messager, java.lang.reflect.Method method) {
        super(messager, null);
        this.method = method;
    }

    @Override
    public String getSimpleName() {
        return method.getName();
    }

    @Override
    public List<AnnotationType> getAnnotationTypes() {
        List<AnnotationType> result = new ArrayList<>();
        for (Annotation annotation : method.getAnnotations())
            result.add(new ReflectionAnnotationType(annotation.annotationType()));
        return result;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return method.getAnnotation(type);
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> result = new ArrayList<>();
        for (int i = 0; i < method.getParameterTypes().length; i++)
            result.add(new ReflectionParameter(method.getParameters()[i]));
        return result;
    }

    @Override
    public String toString() {
        return "ReflectionMethod:" + method.getDeclaringClass().getName() + "#" + method.getName();
    }
}
