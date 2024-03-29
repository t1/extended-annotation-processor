package com.github.t1.exap;

import com.github.t1.exap.reflection.Field;
import com.github.t1.exap.reflection.Package;
import com.github.t1.exap.reflection.ReflectiveFileObject;
import com.github.t1.exap.reflection.Resource;
import com.github.t1.exap.reflection.Type;
import org.slf4j.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.ANNOTATION_TYPE;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.PACKAGE;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

public class Round {
    private static final List<ElementKind> TYPE_KINDS = asList(ENUM, CLASS, ANNOTATION_TYPE, INTERFACE);

    private final Logger log;
    private final ProcessingEnvironment processingEnv;
    private final RoundEnvironment roundEnv;
    private final int roundNumber;

    public Round(Logger log, ProcessingEnvironment processingEnv, RoundEnvironment roundEnv, int roundNumber) {
        this.log = log;
        this.processingEnv = processingEnv;
        this.roundEnv = roundEnv;
        this.roundNumber = roundNumber;
    }

    public List<Field> fieldsAnnotatedWith(Class<? extends Annotation> type) {
        return roundEnv.getElementsAnnotatedWith(type).stream()
            .filter(element -> FIELD == element.getKind())
            .map(element -> Field.of(element, this))
            .collect(toList());
    }

    public List<Type> typesAnnotatedWith(Class<? extends Annotation> type) {
        return roundEnv.getElementsAnnotatedWith(type).stream()
            .filter(element -> TYPE_KINDS.contains(element.getKind()))
            .map(element -> Type.of(element.asType(), this))
            .collect(toList());
    }

    public List<Package> packagesAnnotatedWith(Class<? extends Annotation> type) {
        return roundEnv.getElementsAnnotatedWith(type).stream()
            .filter(element -> PACKAGE == element.getKind())
            .map(element -> new Package((PackageElement) element, this))
            .collect(toList());
    }

    public Logger log() {
        return log;
    }

    /** only for internal use. */
    @Deprecated
    public ProcessingEnvironment env() {
        return processingEnv;
    }

    public int number() {
        return roundNumber;
    }

    public boolean isLast() {
        return roundEnv.processingOver();
    }

    @Override
    public String toString() {
        return "Round#" + roundNumber + "-"
               + ((roundEnv == null) ? "mock" : (roundEnv.getRootElements() + (isLast() ? " [last]" : "")));
    }

    public Package getRootPackage() {
        return new Package(null, this);
    }

    public Package getPackage(String pkg) {
        return new Package(processingEnv.getElementUtils().getPackageElement(pkg), this);
    }

    public Package getPackageOf(Class<?> type) {return getPackage(type.getPackage().getName());}

    /** Generate a resource in `target/generated-resources` */
    public Resource createResource(String relativeName) {
        Path path = Paths.get(getOutputUri()) // target/generated-sources/annotations
            .getParent() // target/generated-sources
            .getParent() // target
            .resolve("generated-resources")
            .resolve(relativeName);
        return new Resource(new ReflectiveFileObject(path));
    }

    private URI getOutputUri() {
        try {
            return processingEnv.getFiler().createResource(CLASS_OUTPUT, "", "dummy").toUri();
        } catch (IOException e) {
            throw new RuntimeException("could not locate " + CLASS_OUTPUT, e);
        }
    }
}
