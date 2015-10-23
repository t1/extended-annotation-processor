package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;
import static java.util.Arrays.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.*;
import javax.tools.Diagnostic;

class ReflectionMethod extends Method {
    private final java.lang.reflect.Method method;
    private List<Parameter> parameters;

    public ReflectionMethod(Type type, java.lang.reflect.Method method) {
        super(ENV, type, DummyProxy.of(ExecutableElement.class));
        this.method = method;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return ReflectionAnnotationWrapper.allOn(method);
    }

    @Override
    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return asList(method.getAnnotationsByType(type));
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return ReflectionAnnotationWrapper.ofTypeOn(method, type);
    }

    @Override
    public List<Parameter> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<>();
            for (int i = 0; i < method.getParameterTypes().length; i++)
                parameters.add(new ReflectionParameter(this, method.getParameters()[i]));
        }
        return parameters;
    }

    @Override
    public Parameter getParameter(int index) {
        return getParameters().get(index);
    }

    @Override
    public Type getReturnType() {
        return Type.of(method.getGenericReturnType());
    }

    @Override
    protected boolean is(Modifier modifier) {
        return Modifiers.on(method.getModifiers()).is(modifier);
    }

    @Override
    protected void message(Diagnostic.Kind kind, CharSequence message) {
        ENV.message(this, kind, message);
    }

    @Override
    public String toString() {
        return "ReflectionMethod:" + method.getDeclaringClass().getName() + "#" + method.getName();
    }
}
