package com.github.t1.exap.reflection;

import com.github.t1.exap.Round;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Repeatable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.t1.exap.reflection.AnnotationPropertyType.ANNOTATION;
import static com.github.t1.exap.reflection.AnnotationPropertyType.BOOLEAN;
import static com.github.t1.exap.reflection.AnnotationPropertyType.BYTE;
import static com.github.t1.exap.reflection.AnnotationPropertyType.CHAR;
import static com.github.t1.exap.reflection.AnnotationPropertyType.CLASS;
import static com.github.t1.exap.reflection.AnnotationPropertyType.DOUBLE;
import static com.github.t1.exap.reflection.AnnotationPropertyType.ENUM;
import static com.github.t1.exap.reflection.AnnotationPropertyType.FLOAT;
import static com.github.t1.exap.reflection.AnnotationPropertyType.INT;
import static com.github.t1.exap.reflection.AnnotationPropertyType.LONG;
import static com.github.t1.exap.reflection.AnnotationPropertyType.SHORT;
import static com.github.t1.exap.reflection.AnnotationPropertyType.STRING;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * It's easiest to call {@link Elemental#getAnnotations(Class)} etc. and then directly use the typesafe, convenient
 * {@link java.lang.annotation.Annotation} object. But this doesn't work for annotation attributes of type Class, as the
 * referenced class is generally not loaded in the annotation processor, only the meta data represented in the
 * {@link TypeMirror}s is. You'd get a {@link javax.lang.model.type.MirroredTypeException} with the message: Attempt to
 * access Class object for TypeMirror. You'll have to use {@link Elemental#getAnnotationWrapper(Class)} for those
 * annotation properties, which returns an instance of this class.
 *
 * @see <a href="http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor">this blog</a>
 */
public class AnnotationWrapper extends Elemental {
    private final AnnotationMirror annotationMirror;

    AnnotationWrapper(AnnotationMirror annotationMirror, Round round) {
        super(round);
        this.annotationMirror = requireNonNull(annotationMirror);
    }

    @Override
    protected Element getElement() {
        return types().asElement(annotationMirror.getAnnotationType());
    }

    public boolean isRepeatable() {
        return getRepeatedAnnotation() != null;
    }

    public AnnotationPropertyType getPropertyType(String name) {
        // see javax.lang.model.element.AnnotationValue
        var property = getProperty(name);
        var type = property instanceof List ? getArrayType(name) : property.getClass();
        var primitivePropertyType = getPrimitivePropertyType(type);
        if (primitivePropertyType != null)
            return primitivePropertyType;
        if (VariableElement.class.isAssignableFrom(type))
            return ENUM;
        if (AnnotationMirror.class.isAssignableFrom(type))
            return ANNOTATION;
        if (TypeMirror.class.isAssignableFrom(type))
            return CLASS;
        throw new UnsupportedOperationException("unexpected property type for property \"" + name + "\" = " + property + " in " + this + " type:" + new TypeInfo(type));
    }

    private Class<?> getArrayType(String name) {
        var list = getAnnotationValueListProperty(name);
        if (list.isEmpty())
            return String.class; // TODO try the method return type instead!
        return list.get(0).getValue().getClass();
    }

    protected AnnotationPropertyType getPrimitivePropertyType(Class<?> type) {
        if (type == Boolean.class || type == boolean.class)
            return BOOLEAN;
        if (type == Byte.class || type == byte.class)
            return BYTE;
        if (type == Character.class || type == char.class)
            return CHAR;
        if (type == Short.class || type == short.class)
            return SHORT;
        if (type == Integer.class || type == int.class)
            return INT;
        if (type == Long.class || type == long.class)
            return LONG;
        if (type == Float.class || type == float.class)
            return FLOAT;
        if (type == Double.class || type == double.class)
            return DOUBLE;
        if (type == String.class)
            return STRING;
        return null;
    }

    private AnnotationMirror getRepeatedAnnotation() {
        for (var m : annotationMirror.getAnnotationType().getAnnotationMirrors())
            if (m.getAnnotationType().toString().equals(Repeatable.class.getName()))
                return m;
        return null;
    }

    public Type getAnnotationType() {
        return Type.of(annotationMirror.getAnnotationType(), round());
    }

    public List<String> getPropertyNames() {
        List<String> result = new ArrayList<>();
        for (var element : annotationMirror.getElementValues().keySet())
            result.add(element.getSimpleName().toString());
        return result;
    }

    public Map<String, Object> getPropertyMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (var entry : annotationMirror.getElementValues().entrySet())
            result.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        return result;
    }

    public <T> T getProperty(String name, Class<T> type) {
        return type.cast(getProperty(name));
    }

    public Object getProperty(String name) {
        var annotationValue = AnnotationWrapperBuilder.getAnnotationValue(annotationMirror, name, round());
        return (annotationValue == null) ? null : annotationValue.getValue();
    }

    protected Object getSingleProperty(String name) {
        var property = getProperty(name);
        return property instanceof List ? getSingleArrayProperty(name) : property;
    }

    protected Object getSingleArrayProperty(String name) {
        var list = (List<?>) getProperty(name);
        if (list.size() != 1)
            throw new IllegalArgumentException(
                    "expected annotation property array to contain exactly one element but found " + list.size());
        return list.get(0);
    }

    @SuppressWarnings("unchecked")
    private List<AnnotationValue> getAnnotationValueListProperty(String name) {
        return (List<AnnotationValue>) getProperty(name);
    }

    public boolean getBooleanProperty(String name) {
        return (boolean) getSingleProperty(name);
    }

    public List<Boolean> getBooleanProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Boolean)
            return singletonList((Boolean) value);
        List<Boolean> list = new ArrayList<>();
        if (value instanceof boolean[])
            for (var b : (boolean[]) value)
                list.add(b);
        else
            for (var annotationValue : getAnnotationValueListProperty(name))
                list.add((Boolean) annotationValue.getValue());
        return list;
    }

    public byte getByteProperty(String name) {
        return (byte) getSingleProperty(name);
    }

    public List<Byte> getByteProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Byte)
            return singletonList((Byte) value);
        List<Byte> list = new ArrayList<>();
        if (value instanceof byte[])
            for (var b : (byte[]) value)
                list.add(b);
        else
            for (var annotationValue : getAnnotationValueListProperty(name))
                list.add((Byte) annotationValue.getValue());
        return list;
    }

    public char getCharProperty(String name) {
        return (char) getSingleProperty(name);
    }

    public List<Character> getCharProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Character)
            return singletonList((Character) value);
        List<Character> list = new ArrayList<>();
        if (value instanceof char[])
            for (var c : (char[]) value)
                list.add(c);
        else
            for (var annotationValue : getAnnotationValueListProperty(name))
                list.add((Character) annotationValue.getValue());
        return list;
    }

    public short getShortProperty(String name) {
        return (short) getSingleProperty(name);
    }

    public List<Short> getShortProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Short)
            return singletonList((Short) value);
        List<Short> list = new ArrayList<>();
        if (value instanceof short[])
            for (var s : (short[]) value)
                list.add(s);
        else
            for (var annotationValue : getAnnotationValueListProperty(name))
                list.add((Short) annotationValue.getValue());
        return list;
    }

    public int getIntProperty(String name) {
        return (int) getSingleProperty(name);
    }

    public List<Integer> getIntProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Integer)
            return singletonList((Integer) value);
        List<Integer> list = new ArrayList<>();
        if (value instanceof int[])
            for (var i : (int[]) value)
                list.add(i);
        else
            for (var annotationValue : getAnnotationValueListProperty(name))
                list.add((Integer) annotationValue.getValue());
        return list;
    }

    public long getLongProperty(String name) {
        return (long) getSingleProperty(name);
    }

    public List<Long> getLongProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Long)
            return singletonList((Long) value);
        List<Long> list = new ArrayList<>();
        if (value instanceof long[])
            for (var l : (long[]) value)
                list.add(l);
        else
            for (var annotationValue : getAnnotationValueListProperty(name))
                list.add((Long) annotationValue.getValue());
        return list;
    }

    public double getDoubleProperty(String name) {
        return (double) getSingleProperty(name);
    }

    public List<Double> getDoubleProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Double)
            return singletonList((Double) value);
        List<Double> list = new ArrayList<>();
        if (value instanceof double[])
            for (var d : (double[]) value)
                list.add(d);
        else
            for (var annotationValue : getAnnotationValueListProperty(name))
                list.add((Double) annotationValue.getValue());
        return list;
    }

    public float getFloatProperty(String name) {
        return (float) getSingleProperty(name);
    }

    public List<Float> getFloatProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Float)
            return singletonList((Float) value);
        List<Float> list = new ArrayList<>();
        if (value instanceof float[])
            for (var f : (float[]) value)
                list.add(f);
        else
            for (var annotationValue : getAnnotationValueListProperty(name))
                list.add((Float) annotationValue.getValue());
        return list;
    }

    public String getStringProperty(String name) {
        return getSingleProperty(name).toString();
    }

    public List<String> getStringProperties(String name) {
        var value = getProperty(name);
        if (value instanceof String)
            return singletonList((String) value);
        List<String> list = new ArrayList<>();
        if (value instanceof String[])
            Collections.addAll(list, (String[]) value);
        else
            for (var annotationValue : getAnnotationValueListProperty(name))
                list.add((String) annotationValue.getValue());
        return list;
    }

    public String getEnumProperty(String name) {
        var variable = (VariableElement) getSingleProperty(name);
        return variable.getSimpleName().toString();
    }

    public List<String> getEnumProperties(String name) {
        var property = getProperty(name);
        if (property instanceof List) {
            var values = getAnnotationValueListProperty(name);
            List<String> list = new ArrayList<>();
            for (var value : values)
                list.add(((VariableElement) value.getValue()).getSimpleName().toString());
            return list;
        } else {
            var value = (VariableElement) property;
            return singletonList(value.getSimpleName().toString());
        }
    }

    public Type getTypeProperty(String name) {
        var value = getSingleProperty(name);
        return Type.of((TypeMirror) value, round());
    }

    public List<Type> getTypeProperties(String name) {
        var property = getProperty(name);
        if (property instanceof List) {
            var values = getAnnotationValueListProperty(name);
            List<Type> list = new ArrayList<>();
            for (var value : values)
                list.add(Type.of((DeclaredType) value.getValue(), round()));
            return list;
        } else {
            var value = (DeclaredType) property;
            return singletonList(Type.of(value, round()));
        }
    }

    public AnnotationWrapper getAnnotationProperty(String name) {
        var value = getProperty(name);
        return new AnnotationWrapper((AnnotationMirror) value, round());
    }

    public List<AnnotationWrapper> getAnnotationProperties(String name) {
        return getPropertyStream(name)
                .map(value -> new AnnotationWrapper((AnnotationMirror) value, round()))
                .collect(toList());
    }

    private Stream<?> getPropertyStream(String name) {
        var property = getProperty(name);
        return (property instanceof List) ? ((List<?>) property).stream() : Stream.of(property);
    }

    @Override
    public String toString() {
        return annotationMirror.toString();
    }
}
