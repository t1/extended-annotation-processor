package com.github.t1.exap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.OTHER;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * Extends the {@link AbstractProcessor} with the handling for the {@link SupportedAnnotationClasses} annotation and
 * other convenience methods.
 */
public abstract class ExtendedAbstractProcessor extends AbstractProcessor {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private int roundNumber = -1;

    /** use {@link #process(Round)} */
    @Override final public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ++roundNumber;

        log.debug("begin round {} (final = {}) of {}", +roundNumber, roundEnv.processingOver(), name());

        try {
            boolean claimed = process(new Round(log, processingEnv, roundEnv, roundNumber));

            log.debug("end round {} of {}", roundNumber, name());

            return claimed;
        } catch (Exception e) {
            String message = e.getClass().getSimpleName() + ((e.getMessage() == null) ? "" : (": " + e.getMessage()));
            log.error("annotation processing round " + roundNumber + " failed: " + message, e);
            var stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            error("annotation processing round " + roundNumber + " failed: " + message + "\n" + stackTrace);
            return true;
        }
    }

    private String name() {
        return getClass().getSimpleName();
    }

    public abstract boolean process(Round round) throws Exception;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        SupportedAnnotationClasses supported = this.getClass().getAnnotation(SupportedAnnotationClasses.class);
        if (supported == null)
            return super.getSupportedAnnotationTypes();

        Set<String> result = new HashSet<>();
        for (Class<? extends Annotation> annotation : supported.value()) {
            result.add(annotation.getName());
        }
        return result;
    }

    private Messager messager() {
        return processingEnv.getMessager();
    }

    public void error(CharSequence message) {
        messager().printMessage(ERROR, message);
    }

    public void warning(CharSequence message) {
        messager().printMessage(WARNING, message);
    }

    public void mandatoryWarning(CharSequence message) {
        messager().printMessage(MANDATORY_WARNING, message);
    }

    public void note(CharSequence message) {
        messager().printMessage(NOTE, message);
    }

    public void otherMessage(CharSequence message) {
        messager().printMessage(OTHER, message);
    }

    public List<? extends Element> getAllMembers(TypeElement type) {
        return processingEnv.getElementUtils().getAllMembers(type);
    }

    public void printElements(Writer writer, Element... elements) {
        processingEnv.getElementUtils().printElements(writer, elements);
    }
}
