package com.github.t1.exap.reflection;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static javax.lang.model.type.TypeKind.DECLARED;

class ReflectionDeclaredTypeMirror implements ReflectionTypeMirror, DeclaredType {
    final java.lang.reflect.Type type;

    ReflectionDeclaredTypeMirror(java.lang.reflect.Type type) {
        this.type = type;
    }

    @Override public String toString() {return type.getTypeName();}

    private Class<?> asClass() {return (Class<?>) type;}

    @Override public AnnotatedElement asAnnotatedElement() {return (AnnotatedElement) type;}

    @Override public TypeKind getKind() {return DECLARED;}

    @Override public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        throw new UnsupportedOperationException("ReflectionDeclaredTypeMirror.accept");
    }

    @Override public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return ReflectionAnnotationMirror.of(asClass().getSimpleName(), asClass().getAnnotations());
    }

    @Override public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return asClass().getAnnotation(annotationType);
    }

    @Override public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return asClass().getAnnotationsByType(annotationType);
    }

    @Override public Element asElement() {
        return new ReflectionTypeElement(ReflectionType.type(type));
    }

    @Override public TypeMirror getEnclosingType() {
        throw new UnsupportedOperationException();
    }

    @Override public List<? extends TypeMirror> getTypeArguments() {
        throw new UnsupportedOperationException();
    }
}
