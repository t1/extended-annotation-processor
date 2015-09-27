package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.ArrayList;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;

class ReflectionType extends Type {
    private static final Map<java.lang.reflect.Type, ReflectionType> types = new HashMap<>();

    static ReflectionType type(java.lang.reflect.Type type) {
        ReflectionType reflectionType = types.get(type);
        if (reflectionType == null) {
            reflectionType = new ReflectionType(ENV, type);
            types.put(type, reflectionType);
        }
        return reflectionType;
    }

    private final java.lang.reflect.Type type;
    private List<Method> methods = null;
    private List<Field> fields = null;

    private ReflectionType(ProcessingEnvironment env, java.lang.reflect.Type type) {
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
    protected boolean is(Modifier modifier) {
        return Modifiers.on(asClass().getModifiers()).is(modifier);
    }

    @Override
    public <T extends java.lang.annotation.Annotation> T getAnnotation(Class<T> annotationType) {
        if (!isClass())
            return null;
        return asClass().getAnnotation(annotationType);
    }

    @Override
    public <T extends java.lang.annotation.Annotation> String getAnnotationClassAttribute(Class<T> annotationType,
            String name) {
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
    public List<Annotation> getAnnotations() {
        return isClass() ? ReflectionAnnotation.allOn(asClass().getAnnotations()) : emptyList();
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
            return Type.of(asClass().getComponentType());
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
        char A = 'A'; // the real type parameter name is not available by reflection; assume <= 26 type params
        List<TypeParameter> list = new ArrayList<>();
        if (isParameterizedType())
            for (java.lang.reflect.Type type : asParameterizedType().getActualTypeArguments())
                list.add(new TypeParameter(Character.toString(A++), asList(Type.of(type))));
        return list;
    }

    @Override
    public List<Method> getMethods() {
        if (methods == null) {
            methods = new ArrayList<>();
            if (isClass())
                for (java.lang.reflect.Method method : asClass().getDeclaredMethods())
                    methods.add(new ReflectionMethod(env(), this, method));
        }
        return methods;
    }

    @Override
    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
            if (isClass())
                for (java.lang.reflect.Field field : asClass().getDeclaredFields())
                    fields.add(new ReflectionField(env(), field));
        }
        return fields;
    }

    @Override
    protected void message(Diagnostic.Kind kind, CharSequence message) {
        ENV.message(this, kind, message);
    }

    @Override
    public Type getSuperType() {
        return isClass() ? Type.of(asClass().getSuperclass()) : null;
    }

    @Override
    public String toString() {
        return "ReflectionType:" + getQualifiedName();
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ReflectionType that = (ReflectionType) obj;
        return this.type == that.type;
    }
}
