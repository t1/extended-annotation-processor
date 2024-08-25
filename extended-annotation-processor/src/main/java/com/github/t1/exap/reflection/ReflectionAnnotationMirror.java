package com.github.t1.exap.reflection;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

class ReflectionAnnotationMirror implements AnnotationMirror {
    public static List<? extends AnnotationMirror> of(String toStringPrefix, Annotation[] annotations) {
        return Stream.of(annotations)
                .map(annotation -> new ReflectionAnnotationMirror(toStringPrefix, annotation))
                .toList();
    }

    private final String toStringPrefix;
    private final Annotation annotation;

    ReflectionAnnotationMirror(String toStringPrefix, Annotation annotation) {
        this.toStringPrefix = toStringPrefix;
        this.annotation = annotation;
    }

    @Override public String toString() {return toStringPrefix + "::" + annotation;}

    @Override
    public DeclaredType getAnnotationType() {return new ReflectionDeclaredTypeMirror(annotation.annotationType());}

    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults() {
        return getElementValues();
    }

    @Override public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues() {
        return Stream.of(annotation.annotationType().getDeclaredMethods())
                .collect(toMap(ReflectionExecutableElement::new, this::value));
    }

    private AnnotationValue value(Method method) {
        var value = new ReflectionAnnotationWrapper(annotation).getProperty(method.getName());
        return new AnnotationValue() {
            @Override public String toString() {return "ReflectionAnnotationMirrorValue[" + getValue() + "]";}

            @Override public Object getValue() {
                return (value instanceof List) ? asAnnotationList(value) // array values have to be represented as a list
                        : (value instanceof Class) ? new ReflectionDeclaredTypeMirror((Class<?>) value) // Classes have to be represented as a TypeMirror
                        : value;
            }

            private List<AnnotationMirror> asAnnotationList(Object value) {
                @SuppressWarnings("unchecked")
                var list = (List<Annotation>) value;
                return list.stream()
                        .map(annotation -> new ReflectionAnnotationMirror(annotation.annotationType().getName(), annotation))
                        .collect(toList());
            }

            @Override public <R, P> R accept(AnnotationValueVisitor<R, P> v, P p) {
                return null;
            }
        };
    }
}
