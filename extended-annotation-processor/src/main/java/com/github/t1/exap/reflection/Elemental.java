package com.github.t1.exap.reflection;

import com.github.t1.exap.*;
import org.slf4j.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.*;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.Objects.*;
import static javax.lang.model.element.Modifier.*;
import static javax.tools.Diagnostic.Kind.*;

public abstract class Elemental {
    private final Round round;
    private final AnnotationWrapperBuilder annotationWrapperBuilder;

    public Elemental(Round round) {
        this.round = requireNonNull(round);
        this.annotationWrapperBuilder = new AnnotationWrapperBuilder(round);
    }

    public Round round() {
        return round;
    }

    @Deprecated
    protected ProcessingEnvironment env() {
        return round.env();
    }

    public Logger log() {
        return round.log();
    }

    protected abstract Element getElement();

    protected Elements elements() {
        return env().getElementUtils();
    }

    protected Types types() {
        return env().getTypeUtils();
    }

    public void error(CharSequence message) {
        message(ERROR, message);
    }

    public void mandatoryWarning(CharSequence message) {
        message(MANDATORY_WARNING, message);
    }

    public void warning(CharSequence message) {
        message(WARNING, message);
    }

    public void note(CharSequence message) {
        message(NOTE, message);
    }

    public void otherMessage(CharSequence message) {
        message(OTHER, message);
    }

    protected void message(Diagnostic.Kind kind, CharSequence message) {
        env().getMessager().printMessage(kind, message, getElement());
    }

    public boolean isPublic() {
        return is(PUBLIC);
    }

    public boolean isStatic() {
        return is(STATIC);
    }

    public boolean isTransient() {
        return is(TRANSIENT);
    }

    protected boolean is(Modifier modifier) {
        return getElement().getModifiers().contains(modifier);
    }

    public <T extends Annotation> boolean isAnnotated(Class<T> type) {
        return !getAnnotations(type).isEmpty();
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        List<T> list = getAnnotations(type);
        if (list.size() == 0)
            return null;
        if (list.size() > 1)
            throw new IllegalArgumentException(
                    "Found " + list.size() + " annotations of type " + type.getName() + " when expecting only one");
        return list.get(0);
    }

    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        if (this.getElement() == null)
            return emptyList();
        T[] annotations = this.getElement().getAnnotationsByType(type);
        if (annotations.length == 0 && JavaDoc.class.equals(type) && docComment() != null)
            return asList(type.cast(javaDoc()));
        return asList(annotations);
    }

    public <T extends Annotation> AnnotationWrapper getAnnotationWrapper(Class<T> type) {
        List<AnnotationWrapper> list = getAnnotationWrappers(type);
        if (list.size() == 0)
            return null;
        if (list.size() > 1)
            throw new IllegalArgumentException(
                    "Found " + list.size() + " annotations of type " + type.getName() + " when expecting only one");
        return list.get(0);
    }

    public List<AnnotationWrapper> getAnnotationWrappers() {
        List<AnnotationWrapper> annotations = annotationWrapperBuilder.allOn(getElement());
        if (!containsJavaDoc(annotations) && docComment() != null)
            annotations.add(0, new ReflectionAnnotationWrapper(javaDoc(), round));
        return annotations;
    }

    private boolean containsJavaDoc(List<AnnotationWrapper> annotations) {
        for (AnnotationWrapper annotation : annotations)
            if (annotation.getAnnotationType().getFullName().equals(JavaDoc.class.getName()))
                return true;
        return false;
    }

    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        List<AnnotationWrapper> annotations = annotationWrapperBuilder.ofTypeOn(getElement(), type.getName());
        if (annotations.isEmpty() && docComment() != null)
            annotations.add(new ReflectionAnnotationWrapper(javaDoc(), round));
        return annotations;
    }

    private JavaDoc javaDoc() {
        return new JavaDoc() {
            private final String docComment = docComment();

            @Override
            public Class<? extends Annotation> annotationType() {
                return JavaDoc.class;
            }

            @Override
            public String value() {
                return docComment.trim();
            }
        };
    }

    public String docComment() {
        return elements().getDocComment(this.getElement());
    }
}
