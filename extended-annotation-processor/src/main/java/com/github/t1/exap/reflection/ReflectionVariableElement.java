package com.github.t1.exap.reflection;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
import static javax.lang.model.element.ElementKind.FIELD;

class ReflectionVariableElement implements VariableElement, ReflectionTypeMirror {
    private final Field field;

    ReflectionVariableElement(Field field) {this.field = field;}

    @Override public AnnotatedElement asAnnotatedElement() {return field;}

    @Override public TypeMirror asType() {return ENV.round().type(field.getType().getCanonicalName()).getTypeMirror();}

    @Override public ElementKind getKind() {return FIELD;}

    @Override public Set<Modifier> getModifiers() {return ReflectionModifiers.on(field.getModifiers()).toSet();}

    @Override public Object getConstantValue() {throw new UnsupportedOperationException();}

    @Override public Name getSimpleName() {return new ReflectionName(field.getName());}

    @Override public Element getEnclosingElement() {throw new UnsupportedOperationException();}

    @Override public List<? extends Element> getEnclosedElements() {throw new UnsupportedOperationException();}

    @Override public List<? extends AnnotationMirror> getAnnotationMirrors() {
        var toStringPrefix = field.getDeclaringClass().getSimpleName() + "#" + field.getName();
        return ReflectionAnnotationMirror.of(toStringPrefix, field.getAnnotations());
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {throw new UnsupportedOperationException();}

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {throw new UnsupportedOperationException();}

    @Override public <R, P> R accept(ElementVisitor<R, P> v, P p) {throw new UnsupportedOperationException();}
}
