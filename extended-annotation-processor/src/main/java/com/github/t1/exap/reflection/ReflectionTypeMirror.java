package com.github.t1.exap.reflection;

import com.github.t1.exap.Round;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableList;
import static javax.lang.model.type.TypeKind.DECLARED;

class ReflectionTypeMirror implements DeclaredType {
    final java.lang.reflect.Type type;
    private final Round round;

    ReflectionTypeMirror(java.lang.reflect.Type type, Round round) {
        this.type = type;
        this.round = round;
    }

    @Override public String toString() {return type.getTypeName();}

    private Class<?> asClass() {return (Class<?>) type;}

    @Override public TypeKind getKind() {return DECLARED;}

    @Override public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        throw new UnsupportedOperationException("ReflectionTypeMirror.accept");
    }

    @Override public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return Stream.of(asClass().getAnnotations())
                .map(annotation -> new ReflectionAnnotationMirror(asClass(), annotation, round))
                .collect(toUnmodifiableList());
    }

    @Override public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return asClass().getAnnotation(annotationType);
    }

    @Override public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return asClass().getAnnotationsByType(annotationType);
    }

    @Override public Element asElement() {
        return new ReflectionTypeElement(ReflectionType.type(type, round), round);
    }

    @Override public TypeMirror getEnclosingType() {
        throw new UnsupportedOperationException();
    }

    @Override public List<? extends TypeMirror> getTypeArguments() {
        throw new UnsupportedOperationException();
    }
}
