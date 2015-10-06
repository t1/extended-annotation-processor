package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;
import static java.util.Arrays.*;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.*;
import javax.tools.Diagnostic;

class ReflectionField extends Field {
    private final java.lang.reflect.Field field;

    public ReflectionField(java.lang.reflect.Field field) {
        super(ENV, DummyProxy.of(VariableElement.class));
        this.field = field;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Type getType() {
        return Type.of(field.getGenericType());
    }

    @Override
    protected boolean is(Modifier modifier) {
        return Modifiers.on(field.getModifiers()).is(modifier);
    }

    @Override
    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return asList(field.getAnnotationsByType(type));
    }

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return ReflectionAnnotationWrapper.allOn(field);
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return ReflectionAnnotationWrapper.ofTypeOn(field, type);
    }

    @Override
    protected void message(Diagnostic.Kind kind, CharSequence message) {
        ENV.message(this, kind, message);
    }

    @Override
    public String toString() {
        return "Field:" + field.getDeclaringClass().getSimpleName() + "#" + field.getName();
    }
}
