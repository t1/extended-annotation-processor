package com.github.t1.exap.reflection;

import static javax.lang.model.element.Modifier.*;
import static javax.tools.Diagnostic.Kind.*;

import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.*;
import javax.tools.Diagnostic;

import com.github.t1.exap.JavaDoc;

public class Elemental {
    private final ProcessingEnvironment processingEnv;
    private final Element element;

    public Elemental(ProcessingEnvironment processingEnv, Element element) {
        this.processingEnv = processingEnv;
        this.element = element;
    }

    public ProcessingEnvironment env() {
        return processingEnv;
    }

    protected Element getElement() {
        return element;
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

    public <T extends java.lang.annotation.Annotation> boolean isAnnotated(Class<T> type) {
        return getAnnotation(type) != null;
    }

    public <T extends java.lang.annotation.Annotation> T getAnnotation(Class<T> type) {
        T annotation = this.getElement().getAnnotation(type);
        if (annotation == null && JavaDoc.class.equals(type) && docComment() != null)
            return type.cast(javaDoc());
        return annotation;
    }

    public List<Annotation> getAnnotations() {
        List<Annotation> result = new ArrayList<>();
        for (AnnotationMirror mirror : getElement().getAnnotationMirrors())
            result.add(Annotation.of(mirror, env()));
        return result;
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
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
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

    /**
     * We can't extract annotation values of type class in an annotation processor, as the class object generally is not
     * loaded, only the meta data as represented in the TypeMirrors. You'd get a
     * {@link javax.lang.model.type.MirroredTypeException} with the message: Attempt to access Class object for
     * TypeMirror.
     * <p>
     * This method returns the <b>fully qualified class name</b> of the annotation 'method' instead; or
     * <code>null</code>, if there is no such 'method' on the annotation.
     * 
     * @see <a href="http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor">
     *      this blog </a>
     */
    public <T extends java.lang.annotation.Annotation> String getAnnotationClassAttribute(Class<T> annotationType,
            String name) {
        for (AnnotationMirror annotationMirror : elements().getAllAnnotationMirrors(element))
            if (annotationType.getName().contentEquals(annotationMirror.getAnnotationType().toString()))
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> annotationProperty //
                : elements().getElementValuesWithDefaults(annotationMirror).entrySet())
                    if (annotationProperty.getKey().getSimpleName().contentEquals(name)) {
                        String className = annotationProperty.getValue().toString();
                        if (className.endsWith(".class"))
                            className = className.substring(0, className.length() - 6);
                        return className;
                    }
        return null;
    }
}
