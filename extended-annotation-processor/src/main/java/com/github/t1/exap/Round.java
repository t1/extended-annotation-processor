package com.github.t1.exap;

import static java.util.Arrays.*;
import static javax.lang.model.element.ElementKind.*;
import static javax.tools.StandardLocation.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import com.github.t1.exap.reflection.*;
import com.github.t1.exap.reflection.Package;

public class Round {
    private static final List<ElementKind> TYPE_KINDS = asList(ENUM, CLASS, ANNOTATION_TYPE, INTERFACE);

    private final ProcessingEnvironment processingEnv;
    private final RoundEnvironment roundEnv;
    private final int roundNumber;

    public Round(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv, int roundNumber) {
        this.processingEnv = processingEnv;
        this.roundEnv = roundEnv;
        this.roundNumber = roundNumber;
    }

    public List<Type> typesAnnotatedWith(Class<? extends Annotation> type) {
        List<Type> result = new ArrayList<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(type))
            if (TYPE_KINDS.contains(element.getKind()))
                result.add(Type.of(element.asType(), processingEnv));
        return result;
    }

    public int number() {
        return roundNumber;
    }

    public boolean isLast() {
        return roundEnv.processingOver();
    }

    @Override
    public String toString() {
        return "Round#" + roundNumber + "-" + roundEnv.getRootElements() + (isLast() ? " [last]" : "");
    }

    public Package getRootPackage() {
        return new Package(processingEnv, null);
    }

    public Resource createResource(String pkg, String relativeName) throws IOException {
        return new Resource(processingEnv.getFiler().createResource(CLASS_OUTPUT, pkg, relativeName));
    }
}
