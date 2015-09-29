package com.github.t1.exap.reflection;

import java.lang.annotation.*;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

import org.slf4j.*;

/**
 * It's easiest to use {@link Elemental#getAnnotation(Class)} and directly use the {@link java.lang.annotation.Annotation}.
 * But this doesn't work for annotation attributes of type Class, as the referenced class is generally not loaded in the annotation processor,
 * only the meta data represented in the {@link TypeMirror}s. You'd get a {@link javax.lang.model.type.MirroredTypeException} with the
 * message: Attempt to access Class object for TypeMirror. You'll have to use {@link Elemental#getAnnotationWrapper(Class)} for those
 * annotation properties, which returns an instance of this class.
 * <p>
 * And if you want to have {@link Repeatable} annotations resolved, you'll also have to use wrappers.
 * 
 * @see <a href="http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor">this blog </a>
 */
public class AnnotationWrapper extends Elemental {
    private static final Logger log = LoggerFactory.getLogger(AnnotationWrapper.class);

    public static List<AnnotationWrapper> allOn(AnnotationMirror mirror, ProcessingEnvironment env) {
        List<AnnotationWrapper> result = new ArrayList<>();
        for (AnnotationMirror annotation : mirror.getAnnotationType().getAnnotationMirrors()) {
            Class<? extends Annotation> repeatedType = getRepeatedType(annotation);
            if (repeatedType == null)
                result.add(new AnnotationWrapper(annotation, env));
            else
                for (Annotation repeated : mirror.getAnnotationsByType(repeatedType))
                    result.add(new AnnotationWrapper(repeated, env));
        }
        return result;
    }

    /** Reverse lookup from the container to the contained class in a {@link Repeatable} annotation */
    private static Class<? extends Annotation> getRepeatedType(AnnotationMirror annotation) {
        return null;
    }

    private final AnnotationMirror mirror;

    public AnnotationWrapper(AnnotationMirror mirror, ProcessingEnvironment env) {
        this(mirror, mirror.getAnnotationType(), env);
    }

    protected AnnotationWrapper(AnnotationMirror mirror, AnnotatedConstruct element, ProcessingEnvironment env) {
        super(env, element);
        this.mirror = mirror;
    }

    public boolean isRepeatable() {
        return getRepeatedAnnotation() != null;
    }

    private AnnotationMirror getRepeatedAnnotation() {
        for (AnnotationMirror m : mirror.getAnnotationType().getAnnotationMirrors())
            if (m.getAnnotationType().toString().equals(Repeatable.class.getName()))
                return m;
        return null;
    }

    public Type getAnnotationType() {
        return Type.of(mirror.getAnnotationType(), env());
    }

    public Map<String, Object> getElementValues() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet())
            result.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        return result;
    }

    public Object get(String name) {
        AnnotationValue value = mirror.getElementValues().get(name);
        return (value == null) ? null : value.getValue();
    }

    public String getString(String name) {
        Object value = get(name);
        return (value == null) ? null : value.toString();
    }

    public Type getType(String name) {
        Object value = get(name);
        return (value == null) ? null : Type.of((TypeMirror) value, env());
    }
}
