package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;

public class ReflectionMethod extends Method implements ReflectionMessageTarget {
    private final java.lang.reflect.Method method;
    private List<Parameter> parameters;

    public ReflectionMethod(ProcessingEnvironment processingEnv, Type type, java.lang.reflect.Method method) {
        super(processingEnv, type, DummyProxy.of(ExecutableElement.class));
        this.method = method;
    }

    @Override
    public String getSimpleName() {
        return method.getName();
    }

    @Override
    public List<AnnotationType> getAnnotationTypes() {
        List<AnnotationType> result = new ArrayList<>();
        for (Annotation annotation : method.getAnnotations())
            result.add(new ReflectionAnnotationType(annotation.annotationType()));
        return result;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return method.getAnnotation(type);
    }

    public ReflectionParameter getParameter(int index) {
        return (ReflectionParameter) getParameters().get(index);
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
    public Type getReturnType() {
        return new ReflectionType(getProcessingEnv(), method.getReturnType());
    }

    @Override
    public String toString() {
        return "ReflectionMethod:" + method.getDeclaringClass().getName() + "#" + method.getName();
    }
}
