package com.github.t1.exap.reflection;

import java.lang.reflect.Modifier;

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
        return new ReflectionType(env(), field.getGenericType());
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(getMember());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(getMember());
    }

    @Override
    public boolean isTransient() {
        return Modifier.isTransient(getMember());
    }

    private int getMember() {
        return field.getModifiers();
    }

    @Override
    public String toString() {
        return "Field:" + field.getDeclaringClass().getSimpleName() + "#" + field.getName();
    }
}
