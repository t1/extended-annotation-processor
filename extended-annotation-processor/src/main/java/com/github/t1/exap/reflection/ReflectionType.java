package com.github.t1.exap.reflection;

import static java.util.Arrays.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

public class ReflectionType extends Type implements ReflectionMessageTarget {
    private final java.lang.reflect.Type type;
    private List<ReflectionMethod> methods;

    public ReflectionType(ProcessingEnvironment env, java.lang.reflect.Type type) {
        super(env, DummyProxy.of(TypeElement.class));
        this.type = type;
    }

    private boolean isClass() {
        return type instanceof Class;
    }

    private Class<?> asClass() {
        return (Class<?>) type;
    }

    private boolean isParameterizedType() {
        return this.type instanceof ParameterizedType;
    }

    private ParameterizedType asParameterizedType() {
        return (ParameterizedType) this.type;
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(asClass().getModifiers());
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        if (!isClass())
            return null;
        return asClass().getAnnotation(annotationType);
    }

    @Override
    public <T extends Annotation> String getAnnotationClassAttribute(Class<T> annotationType, String name) {
        T annotation = getAnnotation(annotationType);
        try {
            java.lang.reflect.Method method = annotationType.getMethod(name);
            Object value = method.invoke(annotation);
            if (value == null)
                return null;
            if (!(value instanceof Class))
                throw new RuntimeException("method " + name + " of annotation " + annotationType.getName()
                        + " does not return a class object");
            return ((Class<?>) value).getName();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("while getting method " + name + " of annotation " + annotationType.getName(),
                    e);
        }
    }

    @Override
    public String getQualifiedName() {
        if (isParameterizedType())
            return asParameterizedType().getRawType().getTypeName();
        return type.getTypeName();
    }

    @Override
    public String getSimpleName() {
        return asClass().getSimpleName();
    }

    @Override
    public boolean isVoid() {
        return void.class.equals(type);
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

    @Override
    public boolean isEnum() {
        return isClass() && asClass().isEnum();
    }

    @Override
    public List<String> getEnumValues() {
        if (!isEnum())
            return null;
        List<String> list = new ArrayList<>();
        for (Object constant : asClass().getEnumConstants())
            list.add(constant.toString());
        return list;
    }

    @Override
    public boolean isArray() {
        return isClass() && asClass().isArray();
    }

    @Override
    public Type elementType() {
        if (isArray())
            return new ReflectionType(env(), asClass().getComponentType());
        return null;
    }

    @Override
    public boolean isAssignableTo(Class<?> type) {
        if (isClass())
            return type.isAssignableFrom(asClass());
        return type.isAssignableFrom((Class<?>) asParameterizedType().getRawType());
    }

    @Override
    public List<TypeParameter> getTypeParameters() {
        char A = 'A'; // the real type parameter name is not available by reflection
        List<TypeParameter> list = new ArrayList<>();
        if (isParameterizedType())
            for (java.lang.reflect.Type type : asParameterizedType().getActualTypeArguments())
                list.add(new TypeParameter(Character.toString(A++), asList(new ReflectionType(env(), type))));
        return list;
    }

    public List<ReflectionMethod> getMethods() {
        if (methods == null) {
            methods = new ArrayList<>();
            for (java.lang.reflect.Method method : asClass().getDeclaredMethods())
                methods.add(new ReflectionMethod(env(), this, method));
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
    public List<Field> getFields() {
        List<Field> fields = new ArrayList<>();
        if (isClass())
            for (java.lang.reflect.Field field : asClass().getDeclaredFields())
                if (!Modifier.isStatic(field.getModifiers()))
                    fields.add(new ReflectionField(env(), field));
        return fields;
    }

    @Override
    public void accept(TypeVisitor scanner) {
        for (Method method : getMethods())
            scanner.visit(method);
    }

    @Override
    public String toString() {
        return "ReflectionType:" + getQualifiedName();
    }
}
