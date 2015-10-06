package com.github.t1.exap.reflection;

import java.lang.annotation.*;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;

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
    public static List<AnnotationWrapper> allOn(Element annotated, ProcessingEnvironment env) {
        List<AnnotationWrapper> result = new ArrayList<>();
        for (AnnotationMirror annotation : mirrors(annotated, env)) {
            TypeMirror repeatedType = getRepeatedType(annotation, env);
            if (repeatedType != null)
                result.addAll(ofTypeOn(annotated, repeatedType.toString(), env));
            else
                result.add(wrapped(annotation, env));
        }
        return result;
    }

    private static List<? extends AnnotationMirror> mirrors(AnnotatedConstruct annotated, ProcessingEnvironment env) {
        if (annotated instanceof DeclaredType)
            annotated = ((DeclaredType) annotated).asElement();
        return mirrors((Element) annotated, env);
    }

    private static List<? extends AnnotationMirror> mirrors(Element annotated, ProcessingEnvironment env) {
        return env.getElementUtils().getAllAnnotationMirrors(annotated);
    }

    /**
     * Reverse lookup from the container to the contained class in a {@link Repeatable} annotation
     * 
     * @param env
     */
    private static TypeMirror getRepeatedType(AnnotationMirror containerAnnotation, ProcessingEnvironment env) {
        List<? extends AnnotationValue> valueList = annotationValuesValue(containerAnnotation);
        if (valueList == null || valueList.isEmpty())
            return null;
        AnnotationValue value = valueList.get(0);
        if (!(value instanceof AnnotationMirror))
            return null;
        // The DeclaredType returned by getAnnotationType doesn't mirror it's annotations! The Element does.
        Element containedType = ((AnnotationMirror) value).getAnnotationType().asElement();
        Repeatable repeatable = containedType.getAnnotation(Repeatable.class);
        if (repeatable == null)
            return null;
        String repeatableValueClassName =
                getAnnotationClassAttribute(env.getElementUtils(), containedType, Repeatable.class, "value");
        if (!containerAnnotation.getAnnotationType().toString().equals(repeatableValueClassName))
            return null;
        return containedType.asType();
    }

    private static <T extends Annotation> String getAnnotationClassAttribute(Elements elements, Element element,
            Class<T> annotationType, String name) {
        for (AnnotationMirror annotationMirror : elements.getAllAnnotationMirrors(element))
            if (annotationType.getName().contentEquals(annotationMirror.getAnnotationType().toString()))
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> annotationProperty //
                : elements.getElementValuesWithDefaults(annotationMirror).entrySet())
                    if (annotationProperty.getKey().getSimpleName().contentEquals(name))
                        return annotationProperty.getValue().getValue().toString();
        return null;
    }

    private static List<? extends AnnotationValue> annotationValuesValue(AnnotationMirror containerAnnotation) {
        Object annotationValue = getAnnotationValue(containerAnnotation, "value");
        if (annotationValue instanceof List) {
            @SuppressWarnings("unchecked")
            List<? extends AnnotationValue> result = (List<? extends AnnotationValue>) annotationValue;
            return result;
        }
        return null;
    }

    private static Object getAnnotationValue(AnnotationMirror annotation, String name) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry //
        : annotation.getElementValues().entrySet())
            if (entry.getKey().getSimpleName().contentEquals(name))
                return entry.getValue().getValue();
        return null;
    }

    public static List<AnnotationWrapper> ofTypeOn(AnnotatedConstruct annotated, String typeName,
            ProcessingEnvironment env) {
        List<AnnotationWrapper> result = new ArrayList<>();
        for (AnnotationMirror annotation : mirrors(annotated, env)) {
            TypeMirror repeatedType = getRepeatedType(annotation, env);
            if (repeatedType != null)
                for (AnnotationValue annotationValue : annotationValuesValue(annotation))
                    result.add(wrapped((AnnotationMirror) annotationValue.getValue(), env));
            else if (isInstance(annotation, typeName))
                result.add(wrapped(annotation, env));
        }
        return result;
    }

    private static AnnotationWrapper wrapped(AnnotationMirror annotation, ProcessingEnvironment env) {
        return new AnnotationWrapper(annotation, env);
    }

    private static boolean isInstance(AnnotationMirror annotation, String typeName) {
        return annotation.getAnnotationType().toString().equals(typeName);
    }

    private final AnnotationMirror annotation;

    private AnnotationWrapper(AnnotationMirror mirror, ProcessingEnvironment env) {
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

    public Map<String, Object> getElementValues() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues()
                .entrySet())
            result.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        return result;
    }

    public Object getValue() {
        return getValue("value");
    }

    public Object getValue(String name) {
        return getAnnotationValue(annotation, name);
    }

    public String getStringValue() {
        return getStringValue("value");
    }

    public String getStringValue(String name) {
        return (String) getValue(name);
    }

    public int getIntValue() {
        return getIntValue("value");
    }

    public int getIntValue(String name) {
        return (int) getValue(name);
    }

    public boolean getBooleanValue() {
        return getBooleanValue("value");
    }

    public boolean getBooleanValue(String name) {
        return (boolean) getValue(name);
    }

    public Type getTypeValue() {
        return getTypeValue("value");
    }

    public Type getTypeValue(String name) {
        Object value = getValue(name);
        return (value == null) ? null : Type.of((TypeMirror) value, env());
    }

    public List<AnnotationWrapper> getAnnotationsValue() {
        return getAnnotationsValue("value");
    }

    public List<AnnotationWrapper> getAnnotationsValue(String name) {
        List<AnnotationWrapper> list = new ArrayList<>();
        Object[] values = (Object[]) getValue(name);
        for (Object value : values)
            list.add(new AnnotationWrapper((AnnotationMirror) value, env()));
        return list;
    }

    @Override
    public String toString() {
        return annotation.toString();
    }
}
