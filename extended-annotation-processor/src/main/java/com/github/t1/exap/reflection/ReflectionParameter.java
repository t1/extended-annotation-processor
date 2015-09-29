package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.*;
import javax.tools.Diagnostic;

class ReflectionParameter extends Parameter {
    private final Method method;
    private final java.lang.reflect.Parameter parameter;

    public ReflectionParameter(Method method, java.lang.reflect.Parameter parameter) {
        super(method, DummyProxy.of(VariableElement.class));
        this.method = method;
        this.parameter = parameter;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public String getName() {
        return parameter.getName();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return parameter.getAnnotation(type);
    }

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return ReflectionAnnotationWrapper.allOn(parameter);
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return ReflectionAnnotationWrapper.ofTypeOn(parameter, type);
    }

    @Override
    public Type getType() {
        return Type.of(parameter.getType());
    }

    @Override
    public boolean isType(Class<?> type) {
        return parameter.getType().equals(type);
    }

    @Override
    protected boolean is(Modifier modifier) {
        return Modifiers.on(parameter.getModifiers()).is(modifier);
    }

    @Override
    protected void message(Diagnostic.Kind kind, CharSequence message) {
        ENV.message(this, kind, message);
    }

    @Override
    public String toString() {
        return "ReflectionParameter:" + parameter + "@" + parameter.getDeclaringExecutable().getName() + "@"
                + parameter.getDeclaringExecutable().getDeclaringClass();
    }
}
