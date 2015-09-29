package com.github.t1.exap.reflection;

import static javax.lang.model.element.Modifier.*;
import static javax.tools.Diagnostic.Kind.*;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.*;
import javax.lang.model.util.*;
import javax.tools.Diagnostic;

import com.github.t1.exap.JavaDoc;

public class Elemental {
    private final ProcessingEnvironment processingEnv;
    private final AnnotatedConstruct element;

    public Elemental(ProcessingEnvironment processingEnv, AnnotatedConstruct element) {
        this.processingEnv = processingEnv;
        this.element = element;
    }

    protected ProcessingEnvironment env() {
        return processingEnv;
    }

    protected Element getElement() {
        return (Element) element;
    }

    protected Elements elements() {
        return processingEnv.getElementUtils();
    }

    protected Types types() {
        return processingEnv.getTypeUtils();
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
        processingEnv.getMessager().printMessage(kind, message, getElement());
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
        return getAnnotation(type) != null;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        T annotation = this.getElement().getAnnotation(type);
        if (annotation == null && JavaDoc.class.equals(type) && docComment() != null)
            return type.cast(javaDoc());
        return annotation;
    }

    public List<AnnotationWrapper> getAnnotationWrappers() {
        List<AnnotationWrapper> result = new ArrayList<>();
        for (AnnotationMirror mirror : getElement().getAnnotationMirrors())
            result.addAll(AnnotationWrapper.allOn(mirror, processingEnv));
        return result;
    }

    public <T extends Annotation> AnnotationWrapper getAnnotationWrapper(Class<T> type) {
        List<AnnotationWrapper> result = getAnnotationWrappers(type);
        if (result.size() == 0)
            return null;
        if (result.size() > 1)
            throw new IllegalArgumentException(
                    "Found " + result.size() + " annotations of type " + type.getName() + " when expecting only one");
        return result.get(0);
    }

    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        List<AnnotationWrapper> result = new ArrayList<>();
        for (AnnotationMirror annotationMirror : getAnnotationMirrors())
            if (equals(type, annotationMirror))
                result.addAll(AnnotationWrapper.allOn(annotationMirror, processingEnv));
        return result;
    }

    private List<? extends AnnotationMirror> getAnnotationMirrors() {
        return (element instanceof Element) ? elements().getAllAnnotationMirrors((Element) element)
                : element.getAnnotationMirrors();
    }

    private <T extends Annotation> boolean equals(Class<T> type, AnnotationMirror annotationMirror) {
        return annotationMirror.getAnnotationType().toString().equals(type.getName());
    }

    private String docComment() {
        return elements().getDocComment(this.getElement());
    }

    private JavaDoc javaDoc() {
        return new JavaDoc() {
            // TODO provide JavaDoc-tags
            // TODO convert JavaDoc-HTML to Markdown
            private final String docComment = docComment();
            private final int firstSentence = docComment.indexOf('.');

            @Override
            public Class<? extends Annotation> annotationType() {
                return JavaDoc.class;
            }

            @Override
            public String summary() {
                return (firstSentence < 0) ? docComment : docComment.substring(0, firstSentence);
            }

            @Override
            public String value() {
                return docComment.trim();
            }
        };
    }
}
