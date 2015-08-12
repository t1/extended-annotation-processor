package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import javax.annotation.processing.Messager;

public class ReflectionType extends Type {
    private final Class<?> container;

    public ReflectionType(Messager messager, Class<?> container) {
        super(messager, null);
        this.container = container;
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(container.getModifiers());
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return container.getAnnotation(type);
    }

    @Override
    public void accept(TypeScanner scanner) {
        for (java.lang.reflect.Method method : container.getDeclaredMethods())
            scanner.visit(new ReflectionMethod(messager, method));
    }

    @Override
    public String toString() {
        return "ReflectionType:" + container.getName();
    }
}
