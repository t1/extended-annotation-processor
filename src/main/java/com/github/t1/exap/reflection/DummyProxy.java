package com.github.t1.exap.reflection;

import java.lang.reflect.*;
import java.lang.reflect.Method;

/**
 * Creates a dummy implementation of any interface that throw an {@link UnsupportedOperationException} on all method
 * invocations, except for {@link #toString()}, {@link #hashCode()}, and {@link #equals(Object)}. Useful only as a
 * reference instance.
 */
public class DummyProxy {
    private static int nextId = 0;

    private static final InvocationHandler createHander(Class<?> type) {
        return new InvocationHandler() {
            private final int id = nextId++;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getParameterTypes().length == 0 && "toString".equals(method.getName()))
                    return toString();
                if (method.getParameterTypes().length == 1 && "equals".equals(method.getName()))
                    return (args[0] == null) ? false : args[0].toString().equals(toString());
                if (method.getParameterTypes().length == 0 && "hashCode".equals(method.getName()))
                    return id;
                throw new UnsupportedOperationException(
                        "invoked unsupported proxy method: " + method + " on type" + type);
            }

            @Override
            public String toString() {
                return "DummyProxy#" + id + ":" + type.getSimpleName();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> T of(Class<T> type) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, createHander(type));
    }
}
