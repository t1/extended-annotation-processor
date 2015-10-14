package com.github.t1.exap.reflection;

import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.lang.annotation.Repeatable;
import java.util.*;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

/**
 * It's easiest to call {@link Elemental#getAnnotations(Class)} etc. and then directly use the typesafe, convenient
 * {@link java.lang.annotation.Annotation} object. But this doesn't work for annotation attributes of type Class, as the
 * referenced class is generally not loaded in the annotation processor, only the meta data represented in the
 * {@link TypeMirror}s is. You'd get a {@link javax.lang.model.type.MirroredTypeException} with the message: Attempt to
 * access Class object for TypeMirror. You'll have to use {@link Elemental#getAnnotationWrapper(Class)} for those
 * annotation properties, which returns an instance of this class.
 * 
 * @see <a href="http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor">this
 *      blog </a>
 */
public class AnnotationWrapper extends Elemental {
    private final AnnotationMirror annotation;

    AnnotationWrapper(AnnotationMirror mirror, ProcessingEnvironment env) {
        this(mirror, mirror.getAnnotationType(), env);
    }

    protected AnnotationWrapper(AnnotationMirror annotation, AnnotatedConstruct element, ProcessingEnvironment env) {
        super(env, element);
        this.annotation = annotation;
    }

    public boolean isRepeatable() {
        return getRepeatedAnnotation() != null;
    }

    private AnnotationMirror getRepeatedAnnotation() {
        for (AnnotationMirror m : annotation.getAnnotationType().getAnnotationMirrors())
            if (m.getAnnotationType().toString().equals(Repeatable.class.getName()))
                return m;
        return null;
    }

    public Type getAnnotationType() {
        return Type.of(annotation.getAnnotationType(), env());
    }

    public List<String> getPropertyNames() {
        List<String> result = new ArrayList<>();
        for (ExecutableElement element : annotation.getElementValues().keySet())
            result.add(element.getSimpleName().toString());
        return result;
    }

    public Map<String, Object> getPropertyMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues()
                .entrySet())
            result.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        return result;
    }

    public Object getProperty(String name) {
        return AnnotationWrapperBuilder.getAnnotationValue(env(), annotation, name);
    }

    public boolean getBooleanProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof boolean[])
            if (((boolean[]) value).length == 1)
                return ((boolean[]) value)[0];
            else
                throw new IllegalArgumentException(
                        "expected boolean[] to contain exactly one element but found " + value);
        return (boolean) value;
    }

    public List<Boolean> getBooleanProperties(String name) {
        List<Boolean> list = new ArrayList<>();
        Object value = getProperty(name);
        if (value instanceof Boolean)
            return singletonList((Boolean) value);
        for (boolean b : (boolean[]) value)
            list.add(b);
        return list;
    }

    public byte getByteProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof byte[])
            if (((byte[]) value).length == 1)
                return ((byte[]) value)[0];
            else
                throw new IllegalArgumentException("expected byte[] to contain exactly one element but found " + value);
        return (byte) value;
    }

    public List<Byte> getByteProperties(String name) {
        List<Byte> list = new ArrayList<>();
        Object value = getProperty(name);
        if (value instanceof Byte)
            return singletonList((Byte) value);
        for (byte b : (byte[]) value)
            list.add(b);
        return list;
    }

    public char getCharProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof char[])
            if (((char[]) value).length == 1)
                return ((char[]) value)[0];
            else
                throw new IllegalArgumentException("expected char[] to contain exactly one element but found " + value);
        return (char) value;
    }

    public List<Character> getCharProperties(String name) {
        List<Character> list = new ArrayList<>();
        Object value = getProperty(name);
        if (value instanceof Character)
            return singletonList((Character) value);
        for (char c : (char[]) value)
            list.add(c);
        return list;
    }

    public short getShortProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof short[])
            if (((short[]) value).length == 1)
                return ((short[]) value)[0];
            else
                throw new IllegalArgumentException(
                        "expected short[] to contain exactly one element but found " + value);
        return (short) value;
    }

    public List<Short> getShortProperties(String name) {
        List<Short> list = new ArrayList<>();
        Object value = getProperty(name);
        if (value instanceof Short)
            return singletonList((Short) value);
        for (short s : (short[]) value)
            list.add(s);
        return list;
    }

    public int getIntProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof int[])
            if (((int[]) value).length == 1)
                return ((int[]) value)[0];
            else
                throw new IllegalArgumentException("expected int[] to contain exactly one element but found " + value);
        return (int) value;
    }

    public List<Integer> getIntProperties(String name) {
        List<Integer> list = new ArrayList<>();
        Object value = getProperty(name);
        if (value instanceof Integer)
            return singletonList((Integer) value);
        for (int i : (int[]) value)
            list.add(i);
        return list;
    }

    public long getLongProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof long[])
            if (((long[]) value).length == 1)
                return ((long[]) value)[0];
            else
                throw new IllegalArgumentException("expected long[] to contain exactly one element but found " + value);
        return (long) value;
    }

    public List<Long> getLongProperties(String name) {
        List<Long> list = new ArrayList<>();
        Object value = getProperty(name);
        if (value instanceof Long)
            return singletonList((Long) value);
        for (long l : (long[]) value)
            list.add(l);
        return list;
    }

    public double getDoubleProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof double[])
            if (((double[]) value).length == 1)
                return ((double[]) value)[0];
            else
                throw new IllegalArgumentException(
                        "expected double[] to contain exactly one element but found " + value);
        return (double) value;
    }

    public List<Double> getDoubleProperties(String name) {
        List<Double> list = new ArrayList<>();
        Object value = getProperty(name);
        if (value instanceof Double)
            return singletonList((Double) value);
        for (double d : (double[]) value)
            list.add(d);
        return list;
    }

    public float getFloatProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof float[])
            if (((float[]) value).length == 1)
                return ((float[]) value)[0];
            else
                throw new IllegalArgumentException(
                        "expected float[] to contain exactly one element but found " + value);
        return (float) value;
    }

    public List<Float> getFloatProperties(String name) {
        List<Float> list = new ArrayList<>();
        Object value = getProperty(name);
        if (value instanceof Float)
            return singletonList((Float) value);
        for (float f : (float[]) value)
            list.add(f);
        return list;
    }

    public String getStringProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof String[])
            if (((String[]) value).length == 1)
                return ((String[]) value)[0];
            else
                throw new IllegalArgumentException(
                        "expected String[] to contain exactly one element but found " + value);
        return (String) value;
    }

    public List<String> getStringProperties(String name) {
        List<String> list = new ArrayList<>();
        Object value = getProperty(name);
        if (value instanceof String)
            return singletonList((String) value);
        for (String s : (String[]) value)
            list.add(s);
        return list;
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> T getEnumProperty(String name) {
        Object value = getProperty(name);
        if (value instanceof Object[])
            if (((T[]) value).length == 1)
                return ((T[]) value)[0];
            else
                throw new IllegalArgumentException("expected Enum[] to contain exactly one element but found " + value);
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> List<T> getEnumProperties(String name) {
        return asList((T[]) getProperty(name));
    }

    public Type getTypeProperty(String name) {
        Object value = getProperty(name);
        // FIXME does this work?
        if (value instanceof TypeMirror[])
            if (((TypeMirror[]) value).length == 1)
                value = ((TypeMirror[]) value)[0];
            else
                throw new IllegalArgumentException("expected type[] to contain exactly one element but found " + value);
        return Type.of((TypeMirror) value, env());
    }

    public List<Type> getTypeProperties(String name) {
        Object value = getProperty(name);
        if (value == null)
            return emptyList();
        List<Type> list = new ArrayList<>();
        // FIXME for(int e : Type.of((TypeMirror) value, env()))
        return list;
    }

    public AnnotationWrapper getAnnotationProperty(String name) {
        Object value = getProperty(name);
        return new AnnotationWrapper((AnnotationMirror) value, env());
    }

    public List<AnnotationWrapper> getAnnotationProperties(String name) {
        List<AnnotationWrapper> list = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<AnnotationMirror> values = (List<AnnotationMirror>) getProperty(name);
        for (AnnotationMirror value : values)
            list.add(new AnnotationWrapper(value, env()));
        return list;
    }

    @Override
    public String toString() {
        return annotation.toString();
    }
}
