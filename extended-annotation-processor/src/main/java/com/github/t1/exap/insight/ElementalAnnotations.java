package com.github.t1.exap.insight;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A convenient wrapper to handle {@link java.lang.reflect.AnnotatedElement}s.
 */
public class ElementalAnnotations {
    private final List<AnnotationWrapper> annotations;

    public ElementalAnnotations(List<AnnotationWrapper> annotations) {
        this.annotations = annotations;
    }

    @Override public String toString() {return annotations.toString();}

    @Override public boolean equals(Object o) {
        return (this == o)
               || ((o instanceof ElementalAnnotations that)
                   && Objects.equals(this.annotations, that.annotations));
    }

    @Override public int hashCode() {return Objects.hashCode(annotations);}

    public Optional<AnnotationWrapper> get(Class<? extends Annotation> annotationType) {
        return get(annotationType.getName());
    }

    public Optional<AnnotationWrapper> get(String name) {
        return annotations.stream()
                .filter(annotationWrapper -> annotationWrapper.getAnnotationType().getFullName().equals(name))
                .findAny();
    }

    public boolean contains(Class<? extends Annotation> annotationType) {
        return get(annotationType).isPresent();
    }

    public ElementalAnnotations merge(ElementalAnnotations other) {
        var out = new ArrayList<>(this.annotations);
        other.annotations.stream()
                .filter(annotationWrapper -> this.annotations.stream().noneMatch(a ->
                        a.getAnnotationType().equals(annotationWrapper.getAnnotationType())))
                .forEach(out::add);
        return new ElementalAnnotations(out);
    }
}
