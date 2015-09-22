package com.github.t1.exap.reflection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;

public class Field extends Elemental {
    private final VariableElement field;

    public Field(ProcessingEnvironment processingEnv, VariableElement field) {
        super(processingEnv, field);
        this.field = field;
    }

    public String getName() {
        return field.getSimpleName().toString();
    }

    public Type getType() {
        return toType(field.asType());
    }
}
