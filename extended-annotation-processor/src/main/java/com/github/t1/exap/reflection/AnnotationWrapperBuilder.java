package com.github.t1.exap.reflection;

import java.lang.annotation.*;
import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

class AnnotationWrapperBuilder {
    static Object getAnnotationValue(ProcessingEnvironment env, AnnotationMirror annotation, String name) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry //
        : env.getElementUtils().getElementValuesWithDefaults(annotation).entrySet())
            if (entry.getKey().getSimpleName().contentEquals(name))
                return entry.getValue().getValue();
        return null;
    }

    private final ProcessingEnvironment env;

    public AnnotationWrapperBuilder(ProcessingEnvironment env) {
        this.env = env;
    }

    public List<AnnotationWrapper> allOn(Element annotated) {
        List<AnnotationWrapper> result = new ArrayList<>();
        for (AnnotationMirror annotation : mirrors(annotated)) {
            TypeMirror repeatedType = getRepeatedType(annotation);
            if (repeatedType != null)
                result.addAll(ofTypeOn(annotated, repeatedType.toString()));
            else
                result.add(wrapped(annotation));
        }
        return result;
    }

    private List<? extends AnnotationMirror> mirrors(AnnotatedConstruct annotated) {
        if (annotated instanceof DeclaredType)
            annotated = ((DeclaredType) annotated).asElement();
        return mirrors((Element) annotated);
    }

    private List<? extends AnnotationMirror> mirrors(Element annotated) {
        return env.getElementUtils().getAllAnnotationMirrors(annotated);
    }

    /**
     * Reverse lookup from the container to the contained class in a {@link Repeatable} annotation
     * 
     * @param env
     */
    private TypeMirror getRepeatedType(AnnotationMirror containerAnnotation) {
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
        String repeatableValueClassName = getAnnotationClassAttribute(containedType, Repeatable.class, "value");
        if (!containerAnnotation.getAnnotationType().toString().equals(repeatableValueClassName))
            return null;
        return containedType.asType();
    }

    private <T extends Annotation> String getAnnotationClassAttribute(Element element, Class<T> annotationType,
            String name) {
        for (AnnotationMirror annotation : env.getElementUtils().getAllAnnotationMirrors(element))
            if (annotationType.getName().contentEquals(annotation.getAnnotationType().toString())) {
                Object value = getAnnotationValue(env, annotation, name);
                return (value == null) ? null : value.toString();
            }
        return null;
    }

    private List<? extends AnnotationValue> annotationValuesValue(AnnotationMirror containerAnnotation) {
        Object annotationValue = getAnnotationValue(env, containerAnnotation, "value");
        if (annotationValue instanceof List) {
            @SuppressWarnings("unchecked")
            List<? extends AnnotationValue> result = (List<? extends AnnotationValue>) annotationValue;
            return result;
        }
        return null;
    }

    public List<AnnotationWrapper> ofTypeOn(AnnotatedConstruct annotated, String typeName) {
        List<AnnotationWrapper> result = new ArrayList<>();
        for (AnnotationMirror annotation : mirrors(annotated)) {
            TypeMirror repeatedType = getRepeatedType(annotation);
            if (repeatedType != null)
                for (AnnotationValue annotationValue : annotationValuesValue(annotation))
                    result.add(wrapped((AnnotationMirror) annotationValue.getValue()));
            else if (isInstance(annotation, typeName))
                result.add(wrapped(annotation));
        }
        return result;
    }

    private AnnotationWrapper wrapped(AnnotationMirror annotation) {
        return new AnnotationWrapper(annotation, env);
    }

    private boolean isInstance(AnnotationMirror annotation, String typeName) {
        return annotation.getAnnotationType().toString().equals(typeName);
    }

}
