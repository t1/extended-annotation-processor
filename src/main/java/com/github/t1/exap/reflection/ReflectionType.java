package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

public class ReflectionType extends Type implements ReflectionMessageTarget {
    private final Class<?> type;
    private List<ReflectionMethod> methods;

    public ReflectionType(ProcessingEnvironment env, Class<?> type) {
        super(env, DummyProxy.of(TypeElement.class));
        this.type = type;
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(type.getModifiers());
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return type.getAnnotation(annotationType);
    }

    @Override
    public String getQualifiedName() {
        return type.getName();
    }

    @Override
    public String getSimpleName() {
        return type.getSimpleName();
    }

    @Override
    public boolean isBoolean() {
        return boolean.class.equals(type) || Boolean.class.equals(type);
    }

    @Override
    public boolean isDecimal() {
        return float.class.equals(type) || Float.class.equals(type) //
                || double.class.equals(type) || Double.class.equals(type);
    }

    @Override
    public boolean isInteger() {
        return byte.class.equals(type) || Byte.class.equals(type) //
                || short.class.equals(type) || Short.class.equals(type) //
                || int.class.equals(type) || Integer.class.equals(type) //
                || long.class.equals(type) || Long.class.equals(type);
    }

    @Override
    public boolean isString() {
        return String.class.equals(type);
    }

    public List<ReflectionMethod> getMethods() {
        if (methods == null) {
            methods = new ArrayList<>();
            for (java.lang.reflect.Method method : type.getDeclaredMethods())
                methods.add(new ReflectionMethod(getProcessingEnv(), this, method));
        }
        return methods;
    }

    public ReflectionMethod getMethod(String name) {
        for (ReflectionMethod method : getMethods())
            if (method.getSimpleName().equals(name))
                return method;
        throw new RuntimeException("method not found: " + name + ".\n  Only knows: " + getMethods());
    }

    @Override
    public void accept(TypeScanner scanner) {
        for (Method method : getMethods())
            scanner.visit(method);
    }

    @Override
    public String toString() {
        return "ReflectionType:" + type.getName();
    }
}
