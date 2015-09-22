package com.github.t1.exap.reflection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;

public class ReflectionField extends Field implements ReflectionMessageTarget {
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
        return new ReflectionType(getProcessingEnv(), field.getGenericType());
    }

    @Override
    public String toString() {
        return "Field:" + field.getDeclaringClass().getSimpleName() + "#" + field.getName();
    }
}
