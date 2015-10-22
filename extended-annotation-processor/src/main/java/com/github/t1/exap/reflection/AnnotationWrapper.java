package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.AnnotationPropertyType.*;
import static java.util.Collections.*;

import java.lang.annotation.Repeatable;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

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
    private final AnnotationMirror annotationMirror;

    AnnotationWrapper(AnnotationMirror annotationMirror, ProcessingEnvironment env) {
        this(annotationMirror, env, env.getTypeUtils().asElement(annotationMirror.getAnnotationType()));
    }

    AnnotationWrapper(AnnotationMirror annotationMirror, ProcessingEnvironment env, AnnotatedConstruct element) {
        super(env, element);
        this.annotationMirror = annotationMirror;
    }

    public boolean isRepeatable() {
        return getRepeatedAnnotation() != null;
    }

    public boolean isArrayProperty(String name) {
        return getProperty(name) instanceof List;
    }

    public AnnotationPropertyType getPropertyType(String name) {
        // see javax.lang.model.element.AnnotationValue
        Class<?> type = isArrayProperty(name) ? getArrayType(name) : getProperty(name).getClass();
        AnnotationPropertyType primitivePropertyType = getPrimitivePropertyType(type);
        if (primitivePropertyType != null)
            return primitivePropertyType;
        if (VariableElement.class.isAssignableFrom(type))
            return ENUM;
        if (AnnotationMirror.class.isAssignableFrom(type))
            return ANNOTATION;
        if (TypeMirror.class.isAssignableFrom(type))
            return CLASS;
        throw new UnsupportedOperationException("unexpected property type for property \"" + name + "\" = "
                + getProperty(name) + " in " + this + " type:" + new TypeInfo(type));
    }

    private Class<?> getArrayType(String name) {
        List<AnnotationValue> list = getAnnotationValueListProperty(name);
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
        for (AnnotationMirror m : annotationMirror.getAnnotationType().getAnnotationMirrors())
            if (m.getAnnotationType().toString().equals(Repeatable.class.getName()))
                return m;
        return null;
    }

    public Type getAnnotationType() {
        return Type.of(annotationMirror.getAnnotationType(), env());
    }

    public List<String> getPropertyNames() {
        List<String> result = new ArrayList<>();
        for (ExecutableElement element : annotationMirror.getElementValues().keySet())
            result.add(element.getSimpleName().toString());
        return result;
    }

    public Map<String, Object> getPropertyMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues()
                .entrySet())
            result.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        return result;
    }

    public Object getProperty(String name) {
        AnnotationValue annotationValue = AnnotationWrapperBuilder.getAnnotationValue(env(), annotationMirror, name);
        return (annotationValue == null) ? null : annotationValue.getValue();
    }

    protected Object getSingleProperty(String name) {
        return isArrayProperty(name) ? getSingleArrayProperty(name) : getProperty(name);
    }

    protected Object getSingleArrayProperty(String name) {
        List<?> list = (List<?>) getProperty(name);
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
        Object value = getProperty(name);
        if (value instanceof Boolean)
            return singletonList((Boolean) value);
        List<Boolean> list = new ArrayList<>();
        if (value instanceof boolean[])
            for (boolean b : (boolean[]) value)
                list.add(b);
        else
            for (AnnotationValue annotationValue : getAnnotationValueListProperty(name))
                list.add((Boolean) annotationValue.getValue());
        return list;
    }

    public byte getByteProperty(String name) {
        return (byte) getSingleProperty(name);
    }

    public List<Byte> getByteProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof Byte)
            return singletonList((Byte) value);
        List<Byte> list = new ArrayList<>();
        if (value instanceof byte[])
            for (byte b : (byte[]) value)
                list.add(b);
        else
            for (AnnotationValue annotationValue : getAnnotationValueListProperty(name))
                list.add((Byte) annotationValue.getValue());
        return list;
    }

    public char getCharProperty(String name) {
        return (char) getSingleProperty(name);
    }

    public List<Character> getCharProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof Character)
            return singletonList((Character) value);
        List<Character> list = new ArrayList<>();
        if (value instanceof char[])
            for (char c : (char[]) value)
                list.add(c);
        else
            for (AnnotationValue annotationValue : getAnnotationValueListProperty(name))
                list.add((Character) annotationValue.getValue());
        return list;
    }

    public short getShortProperty(String name) {
        return (short) getSingleProperty(name);
    }

    public List<Short> getShortProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof Short)
            return singletonList((Short) value);
        List<Short> list = new ArrayList<>();
        if (value instanceof short[])
            for (short s : (short[]) value)
                list.add(s);
        else
            for (AnnotationValue annotationValue : getAnnotationValueListProperty(name))
                list.add((Short) annotationValue.getValue());
        return list;
    }

    public int getIntProperty(String name) {
        return (int) getSingleProperty(name);
    }

    public List<Integer> getIntProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof Integer)
            return singletonList((Integer) value);
        List<Integer> list = new ArrayList<>();
        if (value instanceof int[])
            for (int i : (int[]) value)
                list.add(i);
        else
            for (AnnotationValue annotationValue : getAnnotationValueListProperty(name))
                list.add((Integer) annotationValue.getValue());
        return list;
    }

    public long getLongProperty(String name) {
        return (long) getSingleProperty(name);
    }

    public List<Long> getLongProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof Long)
            return singletonList((Long) value);
        List<Long> list = new ArrayList<>();
        if (value instanceof long[])
            for (long l : (long[]) value)
                list.add(l);
        else
            for (AnnotationValue annotationValue : getAnnotationValueListProperty(name))
                list.add((Long) annotationValue.getValue());
        return list;
    }

    public double getDoubleProperty(String name) {
        return (double) getSingleProperty(name);
    }

    public List<Double> getDoubleProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof Double)
            return singletonList((Double) value);
        List<Double> list = new ArrayList<>();
        if (value instanceof double[])
            for (double d : (double[]) value)
                list.add(d);
        else
            for (AnnotationValue annotationValue : getAnnotationValueListProperty(name))
                list.add((Double) annotationValue.getValue());
        return list;
    }

    public float getFloatProperty(String name) {
        return (float) getSingleProperty(name);
    }

    public List<Float> getFloatProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof Float)
            return singletonList((Float) value);
        List<Float> list = new ArrayList<>();
        if (value instanceof float[])
            for (float f : (float[]) value)
                list.add(f);
        else
            for (AnnotationValue annotationValue : getAnnotationValueListProperty(name))
                list.add((Float) annotationValue.getValue());
        return list;
    }

    public String getStringProperty(String name) {
        return getSingleProperty(name).toString();
    }

    public List<String> getStringProperties(String name) {
        Object value = getProperty(name);
        if (value instanceof String)
            return singletonList((String) value);
        List<String> list = new ArrayList<>();
        if (value instanceof String[])
            for (String s : (String[]) value)
                list.add(s);
        else
            for (AnnotationValue annotationValue : getAnnotationValueListProperty(name))
                list.add((String) annotationValue.getValue());
        return list;
    }

    public String getEnumProperty(String name) {
        VariableElement variable = (VariableElement) getSingleProperty(name);
        return variable.getSimpleName().toString();
    }

    public List<String> getEnumProperties(String name) {
        if (isArrayProperty(name)) {
            List<AnnotationValue> values = getAnnotationValueListProperty(name);
            List<String> list = new ArrayList<>();
            for (AnnotationValue value : values)
                list.add(((VariableElement) value.getValue()).getSimpleName().toString());
            return list;
        } else {
            VariableElement value = (VariableElement) getProperty(name);
            return singletonList(value.getSimpleName().toString());
        }
    }

    public Type getTypeProperty(String name) {
        Object value = getSingleProperty(name);
        return Type.of((TypeMirror) value, env());
    }

    public List<Type> getTypeProperties(String name) {
        if (isArrayProperty(name)) {
            List<AnnotationValue> values = getAnnotationValueListProperty(name);
            List<Type> list = new ArrayList<>();
            for (AnnotationValue value : values)
                list.add(Type.of((DeclaredType) value.getValue(), env()));
            return list;
        } else {
            DeclaredType value = (DeclaredType) getProperty(name);
            return singletonList(Type.of(value, env()));
        }
    }

    public AnnotationWrapper getAnnotationProperty(String name) {
        Object value = getProperty(name);
        return new AnnotationWrapper((AnnotationMirror) value, env());
    }

    public List<AnnotationWrapper> getAnnotationProperties(String name) {
        List<AnnotationWrapper> list = new ArrayList<>();
        if (isArrayProperty(name)) {
            @SuppressWarnings("unchecked")
            List<AnnotationMirror> values = (List<AnnotationMirror>) getProperty(name);
            for (AnnotationMirror value : values)
                list.add(new AnnotationWrapper(value, env()));
        } else {
            AnnotationMirror value = (AnnotationMirror) getProperty(name);
            list.add(new AnnotationWrapper(value, env()));
        }
        return list;
    }

    @Override
    public String toString() {
        return annotationMirror.toString();
    }
}
