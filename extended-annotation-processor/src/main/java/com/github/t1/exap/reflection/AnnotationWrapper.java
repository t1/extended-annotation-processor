package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.AnnotationWrapperBuilder.*;

import java.lang.annotation.Repeatable;
import java.util.*;
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
        return getAnnotationValue(env(), annotation, name);
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
