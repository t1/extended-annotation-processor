package com.github.t1.exap.reflection;

import com.github.t1.exap.Round;

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
    private final Class<?> type;
    private final Annotation annotation;
    private final Round round;

    ReflectionAnnotationMirror(Class<?> type, Annotation annotation, Round round) {
        this.type = type;
        this.annotation = annotation;
        this.round = round;
    }

    @Override public String toString() {return type.getSimpleName() + "::" + annotation;}

    @Override
    public DeclaredType getAnnotationType() {return new ReflectionTypeMirror(annotation.annotationType(), round);}

    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults() {
        return getElementValues();
    }

    @Override public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues() {
        return Stream.of(annotation.annotationType().getDeclaredMethods())
                .collect(toMap(ReflectionExecutableElement::new, this::value));
    }

    private AnnotationValue value(Method method) {
        try {
            var value = method.invoke(annotation);
            return new AnnotationValue() {
                @Override public String toString() {return "ReflectionAnnotationMirrorValue[" + getValue() + "]";}

                @Override public Object getValue() {
                    return (value.getClass().isArray()) ? asAnnotationList() // array values have to be represented as a list
                            : (value instanceof Class) ? new ReflectionTypeMirror((Class<?>) value, round) // Classes have to be represented as a TypeMirror
                            : value;
                }

                private List<AnnotationMirror> asAnnotationList() {
                    // the extra step is necessary for the reflection
                    return Stream.of((Annotation[]) value)
                            .map(annotation -> new ReflectionAnnotationMirror(annotation.annotationType(), annotation, round))
                            .collect(toList());
                }

                @Override public <R, P> R accept(AnnotationValueVisitor<R, P> v, P p) {
                    return null;
                }
            };
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("could not get value from " + method + " in " + annotation, e);
        }
    }
}
