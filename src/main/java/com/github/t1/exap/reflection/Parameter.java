package com.github.t1.exap.reflection;

import java.lang.annotation.Annotation;

import javax.lang.model.element.VariableElement;

public class Parameter {
    private final VariableElement param;

    public Parameter(VariableElement param) {
        this.param = param;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return param.getAnnotation(type);
    }

    @Override
    public String toString() {
        return "Parameter:" + param.getSimpleName() + "#" + param.getEnclosingElement().getSimpleName();
    }
}
