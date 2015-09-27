package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;

import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;

class ReflectionMethod extends Method {
    private final java.lang.reflect.Method method;
    private List<Parameter> parameters;

    public ReflectionMethod(ProcessingEnvironment processingEnv, Type type, java.lang.reflect.Method method) {
        super(processingEnv, type, DummyProxy.of(ExecutableElement.class));
        this.method = method;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public List<Annotation> getAnnotations() {
        return ReflectionAnnotation.allOn(method.getAnnotations());
    }

    @Override
    public <T extends java.lang.annotation.Annotation> T getAnnotation(Class<T> type) {
        return method.getAnnotation(type);
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
        return Type.of(method.getReturnType());
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
