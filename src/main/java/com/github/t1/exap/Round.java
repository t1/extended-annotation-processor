package com.github.t1.exap;

import static javax.lang.model.element.ElementKind.*;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import com.github.t1.exap.reflection.Type;

public class Round {
    private final Messager messager;
    private final RoundEnvironment roundEnv;
    private final int roundNumber;

    public Round(Messager messager, RoundEnvironment roundEnv, int roundNumber) {
        this.messager = messager;
        this.roundEnv = roundEnv;
        this.roundNumber = roundNumber;
    }

    public List<Type> typesAnnotatedWith(Class<? extends Annotation> type) {
        List<Type> result = new ArrayList<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(type))
            if (element.getKind() == CLASS)
                result.add(new Type(messager, (TypeElement) element));
        return result;
    }

    public boolean isLast() {
        return roundEnv.processingOver();
    }

    @Override
    public String toString() {
        return "Round#" + roundNumber + "-" + roundEnv.getRootElements();
    }
}
