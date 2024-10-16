package com.github.t1.exap.reflection;

import com.github.t1.exap.insight.AnnotationWrapper;
import com.github.t1.exap.insight.Field;
import com.github.t1.exap.insight.Method;
import com.github.t1.exap.insight.Package;
import com.github.t1.exap.insight.Type;

import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
import static java.util.Arrays.asList;

class ReflectionType extends Type {
    private static final Map<java.lang.reflect.Type, ReflectionType> types = new HashMap<>();

    static ReflectionType type(java.lang.reflect.Type type) {
        return types.computeIfAbsent(type, ReflectionType::new);
    }

    private final java.lang.reflect.Type type;
    private List<Method> methods = null;
    private List<Method> staticMethods = null;
    private List<Field> fields = null;
    private List<Field> staticFields = null;

    private ReflectionType(java.lang.reflect.Type type) {
        super(new ReflectionDeclaredTypeMirror(type), ENV.round());
        this.type = type;
    }

    @Override
    public Package getPackage() {
        return ReflectionPackage.of(rawType(), round());
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
        return ReflectionModifiers.on(rawType().getModifiers()).is(modifier);
    }

    @Override
    public <T extends Annotation> List<T> getAnnotations(Class<T> annotationType) {
        return asList(rawType().getAnnotationsByType(annotationType));
    }

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return ReflectionAnnotationWrapper.allOn(rawType());
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return ReflectionAnnotationWrapper.ofTypeOn(rawType(), type);
    }

    @Override public String getSimpleName() {return rawType().getSimpleName();}

    @Override public String getFullName() {return type.getTypeName();}

    @Override public boolean isVoid() {return void.class.equals(type) || Void.class.equals(type);}

    @Override public boolean isPrimitive() {return rawType().isPrimitive();}

    @Override public boolean isBoolean() {return boolean.class.equals(type) || Boolean.class.equals(type);}

    @Override public boolean isCharacter() {return char.class.equals(type) || Character.class.equals(type);}

    @Override
    public boolean isFloating() {
        return float.class.equals(type) || Float.class.equals(type)
               || double.class.equals(type) || Double.class.equals(type);
    }

    @Override
    public boolean isInteger() {
        return byte.class.equals(type) || Byte.class.equals(type)
               || short.class.equals(type) || Short.class.equals(type)
               || int.class.equals(type) || Integer.class.equals(type)
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
        return rawType().isArray();
    }

    @Override
    public Type elementType() {
        if (isArray())
            return ReflectionType.type(asClass().getComponentType());
        return null;
    }

    @Override
    public boolean isA(Class<?> thatClass) {
        return thatClass.isAssignableFrom(rawType());
    }

    @Override
    public List<Type> getTypeParameters() {
        List<Type> list = new ArrayList<>();
        if (isParameterizedType())
            for (java.lang.reflect.Type type : asParameterizedType().getActualTypeArguments())
                list.add(ReflectionType.type(type));
        return list;
    }

    @Override
    public List<Method> getMethods() {
        if (methods == null)
            methods = getMethods(false);
        return methods;
    }

    @Override
    public List<Method> getStaticMethods() {
        if (staticMethods == null)
            staticMethods = getMethods(true);
        return staticMethods;
    }

    private List<Method> getMethods(boolean isStatic) {
        List<Method> methods = new ArrayList<>();
        for (java.lang.reflect.Method method : rawType().getDeclaredMethods())
            if (isStatic(method) == isStatic)
                methods.add(new ReflectionMethod(this, method));
        return methods;
    }

    @Override
    public List<Field> getFields() {
        if (fields == null)
            fields = getFields(false);
        return fields;
    }

    @Override
    public List<Field> getStaticFields() {
        if (staticFields == null)
            staticFields = getFields(true);
        return staticFields;
    }

    private List<Field> getFields(boolean isStatic) {
        return Arrays.stream(rawType().getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .map(field -> (Field) new ReflectionField(this, field))
                .filter(field -> field.isStatic() == isStatic)
                .toList();
    }

    private boolean isStatic(java.lang.reflect.Member member) {
        return java.lang.reflect.Modifier.isStatic(member.getModifiers());
    }

    @Override
    public Optional<Type> superType() {
        return Optional.ofNullable(rawType().getSuperclass()).map(ReflectionType::type);
    }

    @Override
    protected void message(Diagnostic.Kind kind, CharSequence message) {
        ENV.message(this, kind, message);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        ReflectionType that = (ReflectionType) obj;
        return this.type.equals(that.type);
    }
}
