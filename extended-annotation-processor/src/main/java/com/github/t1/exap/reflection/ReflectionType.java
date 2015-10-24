package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.ArrayList;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
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
        super(env, DummyProxy.of(TypeMirror.class));
        this.type = type;
    }

    private boolean isClass() {
        return type instanceof Class;
    }

    private Class<?> asClass() {
        return (Class<?>) type;
    }

    private Class<?> rawType() {
        return isClass() ? asClass() : (Class<?>) asParameterizedType().getRawType();
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
    public <T extends Annotation> List<T> getAnnotations(Class<T> annotationType) {
        if (!isClass())
            return emptyList();
        return asList(asClass().getAnnotationsByType(annotationType));
    }

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return isClass() ? ReflectionAnnotationWrapper.allOn(asClass()) : emptyList();
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return isClass() ? ReflectionAnnotationWrapper.ofTypeOn(asClass(), type) : emptyList();
    }

    @Override
    public String getSimpleName() {
        if (type instanceof ParameterizedType)
            return ((Class<?>) ((ParameterizedType) type).getRawType()).getSimpleName();
        return isClass() ? asClass().getSimpleName() : type.toString();
    }

    @Override
    public String getFullName() {
        return type.getTypeName();
    }

    @Override
    public boolean isVoid() {
        return void.class.equals(type) || Void.class.equals(type);
    }

    @Override
    public boolean isPrimitive() {
        return isClass() && asClass().isPrimitive();
    }

    @Override
    public boolean isBoolean() {
        return boolean.class.equals(type) || Boolean.class.equals(type);
    }

    @Override
    public boolean isCharacter() {
        return char.class.equals(type) || Character.class.equals(type);
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
            list.add(((Enum<?>) constant).name());
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
    public boolean isA(Class<?> thatClass) {
        return thatClass.isAssignableFrom(rawType());
    }

    @Override
    public boolean isA(Type that) {
        Class<?> thatClass = ((ReflectionType) that).asClass();
        return isA(thatClass);
    }

    @Override
    public List<Type> getTypeParameters() {
        List<Type> list = new ArrayList<>();
        if (isParameterizedType())
            for (java.lang.reflect.Type type : asParameterizedType().getActualTypeArguments())
                list.add(Type.of(type));
        return list;
    }

    @Override
    public List<Method> getMethods() {
        if (methods == null) {
            methods = new ArrayList<>();
            if (isClass())
                for (java.lang.reflect.Method method : asClass().getDeclaredMethods())
                    methods.add(new ReflectionMethod(this, method));
        }
        return methods;
    }

    @Override
    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
            if (isClass())
                for (java.lang.reflect.Field field : asClass().getDeclaredFields())
                    fields.add(new ReflectionField(field));
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
        return "ReflectionType:" + getFullName();
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
