package com.github.t1.exap.reflection;

import static javax.lang.model.element.Modifier.*;
import static javax.tools.Diagnostic.Kind.*;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.*;

import com.github.t1.exap.JavaDoc;

class Elemental {
    private final ProcessingEnvironment processingEnv;
    private final Element element;

    public Elemental(ProcessingEnvironment processingEnv, Element element) {
        this.processingEnv = processingEnv;
        this.element = element;
    }

    public ProcessingEnvironment getProcessingEnv() {
        return processingEnv;
    }

    public Element getElement() {
        return element;
    }

    protected Elements getElementUtils() {
        return processingEnv.getElementUtils();
    }

    protected Types getTypeUtils() {
        return processingEnv.getTypeUtils();
    }

    protected Type toType(TypeMirror typeMirror) {
        return new Type(getProcessingEnv(), (TypeElement) getTypeUtils().asElement(typeMirror));
    }

    public void error(CharSequence message) {
        messager().printMessage(ERROR, message, getElement());
    }

    public void warning(CharSequence message) {
        messager().printMessage(WARNING, message, getElement());
    }

    public void note(CharSequence message) {
        messager().printMessage(NOTE, message, getElement());
    }

    public Messager messager() {
        return processingEnv.getMessager();
    }

    public List<AnnotationType> getAnnotationTypes() {
        List<AnnotationType> result = new ArrayList<>();
        for (AnnotationMirror mirror : getElement().getAnnotationMirrors()) {
            TypeElement annotation = (TypeElement) mirror.getAnnotationType().asElement();
            result.add(new AnnotationType(annotation));
        }
        return result;
    }

    public boolean isPublic() {
        return getElement().getModifiers().contains(PUBLIC);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean isAnnotated(Class<?> type) {
        return getAnnotation((Class) type) != null;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        T annotation = this.getElement().getAnnotation(type);
        if (annotation == null && JavaDoc.class.equals(type) && docComment() != null)
            return type.cast(javaDoc());
        return annotation;
    }

    private String docComment() {
        return getElementUtils().getDocComment(this.getElement());
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
                return docComment;
            }
        };
    }
}
