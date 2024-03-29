package com.github.t1.exap.reflection;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public class ReflectionTypeElement implements TypeElement {
    private Type type;

    public ReflectionTypeElement(Type type) {this.type = type;}

    @Override public List<? extends Element> getEnclosedElements() {
        return null;
    }

    @Override public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return null;
    }

    @Override public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return null;
    }

    @Override public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return null;
    }

    @Override public NestingKind getNestingKind() {
        return null;
    }

    @Override public Name getQualifiedName() {
        return null;
    }

    @Override public TypeMirror asType() {
        return null;
    }

    @Override public ElementKind getKind() {
        return null;
    }

    @Override public Set<Modifier> getModifiers() {
        return null;
    }

    @Override public Name getSimpleName() {
        return null;
    }

    @Override public TypeMirror getSuperclass() {
        return null;
    }

    @Override public List<? extends TypeMirror> getInterfaces() {
        return null;
    }

    @Override public List<? extends TypeParameterElement> getTypeParameters() {
        return null;
    }

    @Override public Element getEnclosingElement() {
        return null;
    }
}
