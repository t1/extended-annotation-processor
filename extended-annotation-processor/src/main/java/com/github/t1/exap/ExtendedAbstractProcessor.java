package com.github.t1.exap;

import static javax.tools.Diagnostic.Kind.*;

import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import org.slf4j.*;

/**
 * Extends the {@link AbstractProcessor} with the handling for the {@link SupportedAnnotationClasses} annotation and
 * other convenience methods.
 */
public abstract class ExtendedAbstractProcessor extends AbstractProcessor {
    private static final Logger log = LoggerFactory.getLogger(ExtendedAbstractProcessor.class);

    private ProcessingEnvironment env;

    /**
     * This is the poor man's constructor that the designers of the annotation processor API chose, so they can rely on
     * a no-arg constructor and then call this method. It would have been more conventional to simply have a constructor
     * with the env argument.
     */
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        this.env = env;
        super.init(env);
    }

    private int roundNumber = -1;

    /** use {@link #process(Round)} */
    @Override
    final public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ++roundNumber;

        log.debug("begin round {} (final = {}) of {}", +roundNumber, roundEnv.processingOver(), name());

        try {
            boolean claimed = process(new Round(env, roundEnv, roundNumber));

            log.debug("end round {} of {}", roundNumber, name());

            return claimed;
        } catch (Exception e) {
            log.error("annotation processing round " + roundNumber + " failed", e);
            error("annotation processing round " + roundNumber + " failed: " + e.getMessage());
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
        return env.getMessager();
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

    public Filer filer() {
        return env.getFiler();
    }

    public String getDocComment(Element e) {
        return env.getElementUtils().getDocComment(e);
    }

    public List<? extends Element> getAllMembers(TypeElement type) {
        return env.getElementUtils().getAllMembers(type);
    }

    public void printElements(Writer writer, Element... elements) {
        env.getElementUtils().printElements(writer, elements);
    }
}
