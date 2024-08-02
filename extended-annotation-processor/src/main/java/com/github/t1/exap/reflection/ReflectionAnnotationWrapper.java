package com.github.t1.exap.reflection;

import com.github.t1.exap.Round;
import com.github.t1.exap.insight.AnnotationPropertyType;
import com.github.t1.exap.insight.AnnotationWrapper;
import com.github.t1.exap.insight.Type;
import com.github.t1.exap.insight.TypeInfo;

import javax.lang.model.element.AnnotationMirror;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.t1.exap.insight.AnnotationPropertyType.ANNOTATION;
import static com.github.t1.exap.insight.AnnotationPropertyType.CLASS;
import static com.github.t1.exap.insight.AnnotationPropertyType.ENUM;
import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

class ReflectionAnnotationWrapper extends AnnotationWrapper {
    private static final Map<AnnotatedElement, List<AnnotationWrapper>> annotationsOnType = new HashMap<>();
    private static final Map<AnnotatedElement, Map<Class<?>, List<AnnotationWrapper>>> annotationsByType =
            new HashMap<>();

    public static List<AnnotationWrapper> allOn(AnnotatedElement annotated, Round round) {
        return annotationsOnType.computeIfAbsent(annotated, (k) -> {
            List<AnnotationWrapper> result = new ArrayList<>();
            for (Annotation annotation : annotated.getAnnotations()) {
                Class<? extends Annotation> repeatedType = getRepeatedType(annotation);
                if (repeatedType == null)
                    result.add(new ReflectionAnnotationWrapper(annotation, round));
                else
                    result.addAll(ofTypeOn(annotated, repeatedType, round));
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
        @SuppressWarnings({"unchecked"})
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

    public static <T extends Annotation> List<AnnotationWrapper> ofTypeOn(AnnotatedElement annotated, Class<T> type,
                                                                          Round round) {
        Map<Class<?>, List<AnnotationWrapper>> map =
                annotationsByType.computeIfAbsent(annotated, (k) -> new HashMap<>());
        return map.computeIfAbsent(type, (k) -> {
            List<AnnotationWrapper> result = new ArrayList<>();
            for (T annotation : annotated.getAnnotationsByType(type))
                result.add(new ReflectionAnnotationWrapper(annotation, round));
            return result;
        });
    }

    private final Annotation annotation;

    ReflectionAnnotationWrapper(Annotation annotation, Round round) {
        super(ReflectionDummyProxy.of(AnnotationMirror.class), round);
        this.annotation = annotation;
    }

    @Override
    public boolean isRepeatable() {
        return annotation.annotationType().isAnnotationPresent(Repeatable.class);
    }

    @Override
    public Type getAnnotationType() {
        return type(annotation.annotationType());
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
    public List<String> getPropertyNames() {
        List<String> result = new ArrayList<>();
        for (Method method : declaredMethods()) {
            result.add(method.getName());
        }
        return result;
    }

    @Override
    public Map<String, Object> getPropertyMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Method method : declaredMethods()) {
            Object value = invoke(method);
            if (value.getClass().isArray())
                value = arrayToList(value);
            if (value instanceof Class)
                value = type((Class<?>) value);
            result.put(method.getName(), value);
        }
        return result;
    }

    private List<Method> declaredMethods() {
        List<Method> result = new ArrayList<>();
        for (Method method : annotation.annotationType().getDeclaredMethods())
            if (!Annotation.class.equals(method.getDeclaringClass())
                && !Modifier.isStatic(method.getModifiers()))
                result.add(method);
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
                value = type((Class<?>) value);
            list.add(value);
        }
        return list;
    }

    @Override
    public Object getProperty(String name) {
        var value = getRawProperty(name);
        return (value.getClass().isArray()) ? arrayToList(value) : value;
    }

    private Object getRawProperty(String name) {
        var method = getMethod(name);
        return invoke(method);
    }

    private Method getMethod(String name) {
        try {
            return annotation.annotationType().getMethod(name);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AnnotationPropertyType getPropertyType(String name) {
        Object value = getRawProperty(name);
        Class<?> type = value.getClass();
        if (type.isArray())
            type = type.getComponentType();
        AnnotationPropertyType primitivePropertyType = getPrimitivePropertyType(type);
        if (primitivePropertyType != null)
            return primitivePropertyType;
        if (type.isEnum())
            return ENUM;
        if (Annotation.class.isAssignableFrom(type))
            return ANNOTATION;
        if (type.isAssignableFrom(Class.class))
            return CLASS;
        throw new UnsupportedOperationException("unexpected property type for property \"" + name + "\" = " + value
                                                + " in " + this + " type:" + new TypeInfo(type));
    }

    @Override
    protected Object getSingleArrayProperty(String name) {
        List<?> value = (List<?>) getProperty(name);
        if (value.size() != 1)
            throw new IllegalArgumentException("expected annotation property array to contain exactly one element but found " + value.size());
        return value.get(0);
    }

    @Override
    public String getEnumProperty(String name) {
        Enum<?> e = (Enum<?>) getSingleProperty(name);
        return e.name();
    }

    @Override
    public List<String> getEnumProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof Enum)
            return List.of(((Enum<?>) value).name());
        //noinspection unchecked
        return ((List<Enum<?>>) value).stream()
                .map(Enum::name)
                .collect(toList());
    }

    @Override
    public Type getTypeProperty(String name) {
        var typeProperty = getSingleProperty(name);
        return toType(typeProperty);
    }

    private Type toType(Object typeProperty) {
        return (typeProperty instanceof Type) ? (Type) typeProperty : type((Class<?>) typeProperty);
    }

    private ReflectionType type(Class<?> value) {
        return ReflectionType.type(value, round());
    }

    @Override
    public List<Type> getTypeProperties(String name) {
        Object value = getProperty(name);
        if (value == null)
            return List.of();
        if (value instanceof Class)
            return singletonList(type((Class<?>) value));
        return ((List<?>) value).stream().map(this::toType).collect(toList());
    }

    @Override
    public AnnotationWrapper getAnnotationProperty(String name) {
        Annotation value = (Annotation) getSingleProperty(name);
        return new ReflectionAnnotationWrapper(value, round());
    }

    @Override
    public List<AnnotationWrapper> getAnnotationProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof Annotation)
            return singletonList(new ReflectionAnnotationWrapper((Annotation) value, round()));
        var list = new ArrayList<AnnotationWrapper>();
        //noinspection unchecked
        for (var annotation : (List<Annotation>) value)
            list.add(new ReflectionAnnotationWrapper(annotation, round()));
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
