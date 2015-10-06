package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.util.*;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;

class ReflectionAnnotationWrapper extends AnnotationWrapper {
    public static List<AnnotationWrapper> allOn(AnnotatedElement annotated) {
        List<AnnotationWrapper> result = new ArrayList<>();
        for (Annotation annotation : annotated.getAnnotations()) {
            Class<? extends Annotation> repeatedType = getRepeatedType(annotation);
            if (repeatedType == null)
                result.add(wrapped(annotation));
            else
                result.addAll(ofTypeOn(annotated, repeatedType));
        }
        return result;
    }

    /** Reverse lookup from the container to the contained class in a {@link Repeatable} annotation */
    private static Class<? extends Annotation> getRepeatedType(Annotation annotation) {
        Method method = valueMethod(annotation.annotationType());
        if (method == null)
            return null;
        Class<?> returnType = method.getReturnType();
        if (!returnType.isArray())
            return null;
        Class<?> valueType = returnType.getComponentType();
        @SuppressWarnings({ "unchecked" })
        Class<? extends Annotation> repeatedType = (Class<? extends Annotation>) valueType;
        Repeatable repeatable = repeatedType.getAnnotation(Repeatable.class);
        if (repeatable == null)
            return null;
        Class<? extends Annotation> repeatableValue = repeatable.value();
        if (!annotation.annotationType().equals(repeatableValue))
            return null;
        return repeatedType;
    }

    private static Method valueMethod(Class<?> type) {
        for (Method method : type.getMethods())
            if ("value".equals(method.getName()) && 0 == method.getParameterTypes().length)
                return method;
        return null;
    }

    public static <T extends Annotation> List<AnnotationWrapper> ofTypeOn(AnnotatedElement annotated, Class<T> type) {
        List<AnnotationWrapper> result = new ArrayList<>();
        for (T annotation : annotated.getAnnotationsByType(type))
            result.add(wrapped(annotation));
        return result;
    }

    private static ReflectionAnnotationWrapper wrapped(Annotation annotation) {
        return new ReflectionAnnotationWrapper(annotation);
    }

    private final Annotation annotation;

    private ReflectionAnnotationWrapper(Annotation annotation) {
        super(DummyProxy.of(AnnotationMirror.class), DummyProxy.of(AnnotatedConstruct.class), ENV);
        this.annotation = annotation;
    }

    @Override
    public boolean isRepeatable() {
        return annotation.annotationType().isAnnotationPresent(Repeatable.class);
    }

    @Override
    public Type getAnnotationType() {
        return Type.of(annotation.annotationType());
    }

    @Override
    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return getAnnotationType().getAnnotations(type);
    }

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return getAnnotationType().getAnnotationWrappers();
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return getAnnotationType().getAnnotationWrappers(type);
    }

    @Override
    public Map<String, Object> getElementValues() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            if (Annotation.class.equals(method.getDeclaringClass()))
                continue;
            result.put(method.getName(), invoke(method));
        }
        return result;
    }

    private Object invoke(Method method) {
        try {
            return method.invoke(annotation);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("while invoking " + method + " on " + annotation, e);
        }
    }

    @Override
    public Object getValue(String name) {
        try {
            Method method = annotation.annotationType().getMethod(name);
            return invoke(method);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Type getTypeValue(String name) {
        Object value = getValue(name);
        return (value == null) ? null : Type.of((Class<?>) value);
    }

    @Override
    public List<AnnotationWrapper> getAnnotationsValue(String name) {
        List<AnnotationWrapper> list = new ArrayList<>();
        Object[] values = (Object[]) getValue(name);
        for (Object value : values)
            list.add(new ReflectionAnnotationWrapper((Annotation) value));
        return list;
    }

    @Override
    public String toString() {
        return annotation.toString();
    }
}
