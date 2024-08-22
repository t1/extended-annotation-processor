package com.github.t1.exap.reflection;

import com.github.t1.exap.Round;
import com.github.t1.exap.insight.Type;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class ReflectionTypeElement implements TypeElement {
    final Type type;
    final Round round;

    public ReflectionTypeElement(Type type, Round round) {
        this.type = type;
        this.round = round;
    }

    @Override public List<? extends Element> getEnclosedElements() {
        return List.of();
    }

    @Override public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return type.getTypeMirror().getAnnotationMirrors();
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

    @Override public Name getQualifiedName() {return new ReflectionName(type.getFullName());}

    @Override public TypeMirror asType() {return type.getTypeMirror();}

    @Override public ElementKind getKind() {
        switch (type.getKind()) {
            case DECLARED:
                return ElementKind.CLASS;
            default:
                throw new RuntimeException("unsupported type kind: " + type.getKind());
        }
    }

    @Override public Set<Modifier> getModifiers() {
        return null;
    }

    @Override public Name getSimpleName() {
        return null;
    }

    @Override public TypeMirror getSuperclass() {
        Class<?> superclass = getReflectedClass().getSuperclass();
        return (superclass == null) ? NO_TYPE : new ReflectionDeclaredTypeMirror(superclass, round);
    }

    private Class<?> getReflectedClass() {
        return (Class<?>) ((ReflectionDeclaredTypeMirror) type.getTypeMirror()).type;
    }

    @Override public List<? extends TypeMirror> getInterfaces() {
        return Stream.of(getReflectedClass().getInterfaces())
                .map(i -> new ReflectionDeclaredTypeMirror(i, round))
                .collect(toList());
    }

    @Override public List<? extends TypeParameterElement> getTypeParameters() {
        return null;
    }

    @Override public Element getEnclosingElement() {
        return null;
    }

    private static final TypeMirror NO_TYPE = new NoType() {
        @Override public TypeKind getKind() {return TypeKind.NONE;}

        @Override public List<? extends AnnotationMirror> getAnnotationMirrors() {return List.of();}

        @Override public <A extends Annotation> A getAnnotation(Class<A> annotationType) {return null;}

        @Override public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {return null;}

        @Override public <R, P> R accept(TypeVisitor<R, P> v, P p) {throw new UnsupportedOperationException();}
    };
}
