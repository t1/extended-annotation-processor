package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;

public class Method extends Messaged {
    private final ExecutableElement method;

    public Method(Messager messager, ExecutableElement method) {
        super(messager, method);
        this.method = method;
    }

    public String getSimpleName() {
        return method.getSimpleName().toString();
    }

    public List<AnnotationType> getAnnotationTypes() {
        List<AnnotationType> result = new ArrayList<>();
        for (AnnotationMirror mirror : method.getAnnotationMirrors()) {
            TypeElement annotation = (TypeElement) mirror.getAnnotationType().asElement();
            result.add(new AnnotationType(annotation));
        }
        return result;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return method.getAnnotation(type);
    }

    public List<Parameter> getParameters() {
        List<Parameter> result = new ArrayList<>();
        for (VariableElement param : method.getParameters()) {
            result.add(new Parameter(param));
        }
        return result;
    }

    @Override
    public String toString() {
        return "Method:" + method.getEnclosingElement().getSimpleName() + "#" + method.getSimpleName();
    }
}
