package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;

import java.lang.annotation.Annotation;
import java.util.*;

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
    public <T extends Annotation> boolean isAnnotated(Class<T> type) {
        return field.isAnnotationPresent(type);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return field.getAnnotation(type);
    }

    @Override
    public List<AnnotationType> getAnnotationTypes() {
        List<AnnotationType> result = new ArrayList<>();
        for (Annotation annotation : field.getAnnotations())
            result.add(new ReflectionAnnotationType(annotation.annotationType()));
        return result;
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
