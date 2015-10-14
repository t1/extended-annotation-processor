package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.ArrayList;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.tools.Diagnostic;

class ReflectionAnnotationWrapper extends AnnotationWrapper {
    private static Map<AnnotatedElement, List<AnnotationWrapper>> annotationsOnType = new HashMap<>();
    private static final Map<AnnotatedElement, Map<Class<?>, List<AnnotationWrapper>>> annotationsByType =
            new HashMap<>();

    public static List<AnnotationWrapper> allOn(AnnotatedElement annotated) {
        return annotationsOnType.computeIfAbsent(annotated, (k) -> {
            List<AnnotationWrapper> result = new ArrayList<>();
            for (Annotation annotation : annotated.getAnnotations()) {
                Class<? extends Annotation> repeatedType = getRepeatedType(annotation);
                if (repeatedType == null)
                    result.add(wrapped(annotation));
                else
                    result.addAll(ofTypeOn(annotated, repeatedType));
            }
            return result;
        });
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
        Map<Class<?>, List<AnnotationWrapper>> map =
                annotationsByType.computeIfAbsent(annotated, (k) -> new HashMap<>());
        return map.computeIfAbsent(type, (k) -> {
            List<AnnotationWrapper> result = new ArrayList<>();
            for (T annotation : annotated.getAnnotationsByType(type))
                result.add(wrapped(annotation));
            return result;
        });
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
    public List<String> getValueNames() {
        List<String> result = new ArrayList<>();
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            if (Annotation.class.equals(method.getDeclaringClass()))
                continue;
            result.add(method.getName());
        }
        return result;
    }

    @Override
    public Map<String, Object> getValueMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            if (Annotation.class.equals(method.getDeclaringClass()))
                continue;
            Object value = invoke(method);
            if (value.getClass().isArray())
                value = arrayToList(value);
            if (value instanceof Class)
                value = Type.of((Class<?>) value);
            result.put(method.getName(), value);
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

    private Object arrayToList(Object array) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < Array.getLength(array); i++) {
            Object value = Array.get(array, i);
            if (value instanceof Class)
                value = Type.of((Class<?>) value);
            list.add(value);
        }
        return list;
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
        if (value instanceof Class[])
            if (((Class[]) value).length == 1)
                value = ((Class[]) value)[0];
            else
                throw new IllegalArgumentException(
                        "expected Class[] to contain exactly one element but found " + value);
        return Type.of((Class<?>) value);
    }

    @Override
    public List<Type> getTypeValues(String name) {
        Object value = getValue(name);
        if (value instanceof Class)
            return singletonList(Type.of((Class<?>) value));
        List<Type> list = new ArrayList<>();
        if (value != null)
            for (Class<?> t : (Class<?>[]) value)
                list.add(Type.of(t));
        return list;
    }

    @Override
    public <T extends Enum<?>> List<T> getEnumValues() {
        return getEnumValues("value");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> List<T> getEnumValues(String name) {
        Object value = getValue(name);
        if (value instanceof Enum)
            return asList((T) value);
        return asList((T[]) value);
    }

    @Override
    public AnnotationWrapper getAnnotationValue(String name) {
        Object value = getValue(name);
        return new ReflectionAnnotationWrapper((Annotation) value);
    }

    @Override
    public List<AnnotationWrapper> getAnnotationValues(String name) {
        Object value = getValue(name);
        if (value instanceof Annotation)
            return singletonList(new ReflectionAnnotationWrapper((Annotation) value));
        List<AnnotationWrapper> list = new ArrayList<>();
        for (Annotation annotation : (Annotation[]) value)
            list.add(new ReflectionAnnotationWrapper(annotation));
        return list;
    }

    @Override
    protected void message(Diagnostic.Kind kind, CharSequence message) {
        ENV.message(this, kind, message);
    }

    @Override
    public String toString() {
        return annotation.toString();
    }
}
