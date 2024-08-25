package com.github.t1.exap.insight;

import com.github.t1.exap.Round;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Repeatable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.github.t1.exap.insight.AnnotationPropertyType.ANNOTATION;
import static com.github.t1.exap.insight.AnnotationPropertyType.BOOLEAN;
import static com.github.t1.exap.insight.AnnotationPropertyType.BYTE;
import static com.github.t1.exap.insight.AnnotationPropertyType.CHAR;
import static com.github.t1.exap.insight.AnnotationPropertyType.CLASS;
import static com.github.t1.exap.insight.AnnotationPropertyType.DOUBLE;
import static com.github.t1.exap.insight.AnnotationPropertyType.ENUM;
import static com.github.t1.exap.insight.AnnotationPropertyType.FLOAT;
import static com.github.t1.exap.insight.AnnotationPropertyType.INT;
import static com.github.t1.exap.insight.AnnotationPropertyType.LONG;
import static com.github.t1.exap.insight.AnnotationPropertyType.SHORT;
import static com.github.t1.exap.insight.AnnotationPropertyType.STRING;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * It's easiest to call {@link Elemental#getAnnotations(Class)} etc. and then directly use the typesafe, convenient
 * {@link java.lang.annotation.Annotation} object. But this doesn't work for annotation attributes of type Class, as the
 * referenced class is generally not loaded in the annotation processor, only the metadata represented in the
 * {@link TypeMirror}s is. You'd get a {@link javax.lang.model.type.MirroredTypeException} with the message: "Attempt to
 * access Class object for TypeMirror." You'll have to use {@link Elemental#getAnnotationWrapper(Class)} for those
 * annotation properties, which returns an instance of this class.
 *
 * @see <a href="http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor">this blog</a>
 */
public class AnnotationWrapper extends Elemental {
    private final AnnotationMirror annotationMirror;

    public AnnotationWrapper(AnnotationMirror annotationMirror, Round round) {
        super(round);
        this.annotationMirror = requireNonNull(annotationMirror);
    }

    @Override protected Element getElement() {return types().asElement(annotationMirror.getAnnotationType());}

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
        @SuppressWarnings("unchecked")
        var list = (List<AnnotationValue>) getProperty(name);
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

    public Object getSingleProperty(String name) {
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

    public boolean getBooleanProperty(String name) {
        return (boolean) getSingleProperty(name);
    }

    public List<Boolean> getBooleanProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Boolean)
            return List.of((Boolean) value);
        return ((List<?>) value).stream()
                .map(AnnotationWrapper::annotationValue)
                .map(Boolean.class::cast)
                .collect(toList());
    }

    private static Object annotationValue(Object annotationValue) {
        return (annotationValue instanceof AnnotationValue) ? ((AnnotationValue) annotationValue).getValue() : annotationValue;
    }

    public byte getByteProperty(String name) {
        return (byte) getSingleProperty(name);
    }

    public List<Byte> getByteProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Byte)
            return List.of((Byte) value);
        return ((List<?>) value).stream()
                .map(AnnotationWrapper::annotationValue)
                .map(Byte.class::cast)
                .collect(toList());
    }

    public char getCharProperty(String name) {
        return (char) getSingleProperty(name);
    }

    public List<Character> getCharProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Character)
            return List.of((Character) value);
        return ((List<?>) value).stream()
                .map(AnnotationWrapper::annotationValue)
                .map(Character.class::cast)
                .collect(toList());
    }

    public short getShortProperty(String name) {
        return (short) getSingleProperty(name);
    }

    public List<Short> getShortProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Short)
            return List.of((Short) value);
        return ((List<?>) value).stream()
                .map(AnnotationWrapper::annotationValue)
                .map(Short.class::cast)
                .collect(toList());
    }

    public int getIntProperty(String name) {
        return (int) getSingleProperty(name);
    }

    public List<Integer> getIntProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Integer)
            return List.of((Integer) value);
        return ((List<?>) value).stream()
                .map(AnnotationWrapper::annotationValue)
                .map(Integer.class::cast)
                .collect(toList());
    }

    public long getLongProperty(String name) {
        return (long) getSingleProperty(name);
    }

    public List<Long> getLongProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Long)
            return List.of((Long) value);
        return ((List<?>) value).stream()
                .map(AnnotationWrapper::annotationValue)
                .map(Long.class::cast)
                .collect(toList());
    }

    public double getDoubleProperty(String name) {
        return (double) getSingleProperty(name);
    }

    public List<Double> getDoubleProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Double)
            return List.of((Double) value);
        return ((List<?>) value).stream()
                .map(AnnotationWrapper::annotationValue)
                .map(Double.class::cast)
                .collect(toList());
    }

    public float getFloatProperty(String name) {
        return (float) getSingleProperty(name);
    }

    public List<Float> getFloatProperties(String name) {
        var value = getProperty(name);
        if (value instanceof Float)
            return List.of((Float) value);
        return ((List<?>) value).stream()
                .map(AnnotationWrapper::annotationValue)
                .map(Float.class::cast)
                .collect(toList());
    }

    public String getStringProperty(String name) {
        return getSingleProperty(name).toString();
    }

    public List<String> getStringProperties(String name) {
        var value = getProperty(name);
        if (value instanceof String)
            return List.of((String) value);
        return ((List<?>) value).stream()
                .map(AnnotationWrapper::stringValue)
                .collect(toList());
    }

    private static String stringValue(Object annotationValue) {
        if (annotationValue instanceof AnnotationValue)
            annotationValue = ((AnnotationValue) annotationValue).getValue();
        return (annotationValue instanceof String) ? (String) annotationValue : annotationValue.toString();
    }

    public String getEnumProperty(String name) {
        var variable = (VariableElement) getSingleProperty(name);
        return variable.getSimpleName().toString();
    }

    public List<String> getEnumProperties(String name) {
        var property = getProperty(name);
        if (property instanceof List) {
            @SuppressWarnings("unchecked")
            var values = (List<AnnotationValue>) property;
            List<String> list = new ArrayList<>();
            for (var value : values)
                list.add(((VariableElement) value.getValue()).getSimpleName().toString());
            return list;
        } else {
            var value = (VariableElement) property;
            return List.of(value.getSimpleName().toString());
        }
    }

    public Type getTypeProperty(String name) {
        var value = getSingleProperty(name);
        return Type.of((TypeMirror) value, round());
    }

    public Stream<Type> typeProperties(String name) {
        return getTypeProperties(name).stream();
    }

    public List<Type> getTypeProperties(String name) {
        var property = getProperty(name);
        if (property instanceof List) {
            @SuppressWarnings("unchecked")
            var values = (List<AnnotationValue>) property;
            List<Type> list = new ArrayList<>();
            for (var annotationValue : values) {
                var valueObject = annotationValue.getValue();
                TypeMirror typeMirror;
                try {
                    typeMirror = (TypeMirror) valueObject;
                } catch (Exception e) {
                    throw new RuntimeException("can't cast " + valueObject.getClass() + ": " + valueObject, e);
                }
                list.add(Type.of(typeMirror, round()));
            }
            return list;
        } else {
            var value = (TypeMirror) property;
            return List.of(Type.of(value, round()));
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

    @Override public String toString() {return annotationMirror.toString();}

    @Override public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof AnnotationWrapper)) return false;
        return Objects.equals(annotationMirror, ((AnnotationWrapper) that).annotationMirror);
    }

    @Override public int hashCode() {
        return Objects.hashCode(annotationMirror);
    }
}
