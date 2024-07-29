package com.github.t1.exap.reflection;

import com.github.t1.exap.insight.AnnotationWrapper;
import com.github.t1.exap.insight.Method;
import com.github.t1.exap.insight.Parameter;
import com.github.t1.exap.insight.Type;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.List;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
import static java.util.Arrays.asList;

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
    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return asList(parameter.getAnnotationsByType(type));
    }

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return ReflectionAnnotationWrapper.allOn(parameter, round());
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return ReflectionAnnotationWrapper.ofTypeOn(parameter, type, round());
    }

    @Override
    public Type getType() {
        return ReflectionType.type(parameter.getParameterizedType(), round());
    }

    @Override
    protected boolean is(Modifier modifier) {
        return Modifiers.on(parameter.getModifiers()).is(modifier);
    }

    @Override
    protected void message(Diagnostic.Kind kind, CharSequence message) {
        ENV.message(this, kind, message);
    }
}
