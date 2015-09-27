package com.github.t1.exap.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class ReflectionAnnotation extends Annotation {
    public static List<Annotation> allOn(java.lang.annotation.Annotation[] annotations) {
        List<Annotation> result = new ArrayList<>();
        for (java.lang.annotation.Annotation annotation : annotations)
            result.add(new ReflectionAnnotation(annotation));
        return result;
    }

    private final java.lang.annotation.Annotation annotation;

    public ReflectionAnnotation(java.lang.annotation.Annotation annotation) {
        super(null, null);
        this.annotation = annotation;
    }

    @Override
    public Type getAnnotationType() {
        return Type.of(annotation.annotationType());
    }

    @Override
    public Map<String, Object> getElementValues() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            if (java.lang.annotation.Annotation.class.equals(method.getDeclaringClass()))
                continue;
            result.put(method.getName(), invoke(method));
        }
        return result;
    }

    private Object invoke(Method method) {
        try {
            return method.invoke(annotation);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("while invokint " + method + " on " + annotation, e);
        }
    }

    @Override
    public String toString() {
        return annotation.toString();
    }
}
