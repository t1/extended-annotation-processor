package com.github.t1.exap.insight;

import com.github.t1.exap.Round;
import org.slf4j.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.TRANSIENT;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.OTHER;
import static javax.tools.Diagnostic.Kind.WARNING;

public abstract class Elemental {
    private final Round round;
    private final AnnotationWrapperBuilder annotationWrapperBuilder;

    public Elemental(Round round) {
        this.round = requireNonNull(round);
        this.annotationWrapperBuilder = new AnnotationWrapperBuilder(round);
    }

    public Round round() {return round;}

    @Deprecated
    protected ProcessingEnvironment env() {return round.env();}

    public Logger log() {return round.log();}

    public String name() {
        var element = getElement();
        return (element == null) ? toString() : element.getSimpleName().toString();
    }

    protected abstract Element getElement();

    protected Elements elements() {return env().getElementUtils();}

    protected Types types() {return env().getTypeUtils();}

    public void error(CharSequence message) {message(ERROR, message);}

    public void mandatoryWarning(CharSequence message) {message(MANDATORY_WARNING, message);}

    public void warning(CharSequence message) {message(WARNING, message);}

    public void note(CharSequence message) {message(NOTE, message);}

    public void otherMessage(CharSequence message) {message(OTHER, message);}

    protected void message(Diagnostic.Kind kind, CharSequence message) {
        env().getMessager().printMessage(kind, message, getElement());
    }

    public boolean isPackagePrivate() {return !isPublic() && !isProtected() && !isPrivate();}

    public boolean isPrivate() {return is(PRIVATE);}

    public boolean isProtected() {return is(PROTECTED);}

    public boolean isPublic() {return is(PUBLIC);}

    public boolean isAbstract() {return is(ABSTRACT);}

    public boolean isDefault() {return is(DEFAULT);}

    public boolean isStatic() {return is(STATIC);}

    public boolean isTransient() {return is(TRANSIENT);}

    protected boolean is(Modifier modifier) {
        return getElement().getModifiers().contains(modifier);
    }

    public <T extends Annotation> boolean isAnnotated(Class<T> type) {
        return !getAnnotations(type).isEmpty();
    }

    /** Find an annotation on this element or any of its enclosing elements. */
    public <T extends Annotation> Optional<T> findAnnotation(Class<T> type) {
        return annotation(type).or(() -> enclosingElement().flatMap(t -> t.findAnnotation(type)));
    }

    public <T extends Annotation> Optional<T> annotation(Class<T> type) {
        return Optional.ofNullable(getAnnotation(type));
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        List<T> list = getAnnotations(type);
        if (list.isEmpty())
            return null;
        if (list.size() > 1)
            throw new IllegalArgumentException(
                    "Found " + list.size() + " annotations of type " + type.getName() + " when expecting only one");
        return list.get(0);
    }

    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return asList(this.getElement().getAnnotationsByType(type));
    }


    public <T extends Annotation> AnnotationWrapper getAnnotationWrapper(Class<T> type) {
        return annotationWrapper(type).orElse(null);
    }

    public <T extends Annotation> Optional<AnnotationWrapper> annotationWrapper(Class<T> type) {
        List<AnnotationWrapper> list = getAnnotationWrappers(type);
        if (list.isEmpty())
            return Optional.empty();
        if (list.size() > 1)
            throw new IllegalArgumentException(
                    "Found " + list.size() + " annotations of type " + type.getName() + " when expecting only one");
        return Optional.of(list.get(0));
    }

    public Stream<AnnotationWrapper> annotationWrappers() {return getAnnotationWrappers().stream();}

    public List<AnnotationWrapper> getAnnotationWrappers() {return annotationWrapperBuilder.allOn(getElement());}

    public ElementalAnnotations annotations() {return new ElementalAnnotations(getAnnotationWrappers());}

    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return annotationWrapperBuilder.ofTypeOn(getElement(), type.getName());
    }


    public Stream<? extends AnnotationMirror> annotationMirrors() {
        return getElement().getAnnotationMirrors().stream();
    }

    public abstract Optional<Elemental> enclosingElement();

    public Optional<String> javaDoc() {
        return Optional.ofNullable(elements().getDocComment(this.getElement()))
                .map(String::trim);
    }

    public String getJavaDoc() {
        return javaDoc().orElse(null);
    }
}
