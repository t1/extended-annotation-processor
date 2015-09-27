package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;

class ReflectionField extends Field {
    private final java.lang.reflect.Field field;

    public ReflectionField(ProcessingEnvironment env, java.lang.reflect.Field field) {
        super(env, DummyProxy.of(VariableElement.class));
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
    public <T extends java.lang.annotation.Annotation> boolean isAnnotated(Class<T> type) {
        return field.isAnnotationPresent(type);
    }

    @Override
    public <T extends java.lang.annotation.Annotation> T getAnnotation(Class<T> type) {
        return field.getAnnotation(type);
    }

    @Override
    public List<Annotation> getAnnotations() {
        return ReflectionAnnotation.allOn(field.getAnnotations());
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
