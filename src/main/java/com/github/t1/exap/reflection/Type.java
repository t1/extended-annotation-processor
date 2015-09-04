package com.github.t1.exap.reflection;

import static javax.lang.model.element.ElementKind.*;
import static javax.lang.model.type.TypeKind.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;

public class Type extends Elemental {
    private final TypeElement type;

    public Type(ProcessingEnvironment processingEnv, TypeElement type) {
        super(processingEnv, type);
        this.type = type;
    }

    public void accept(TypeScanner scanner) {
        for (Element element : type.getEnclosedElements())
            if (element.getKind() == METHOD)
                scanner.visit(new Method(getProcessingEnv(), this, (ExecutableElement) element));
    }

    @Override
    public String toString() {
        return "Type:" + getQualifiedName();
    }

    public String getQualifiedName() {
        return type.getQualifiedName().toString();
    }

    public String getSimpleName() {
        return type.getSimpleName().toString();
    }

    public boolean isBoolean() {
        return kind() == BOOLEAN;
    }

    public boolean isNumber() {
        return isInteger() || isDecimal();
    }

    public boolean isInteger() {
        return kind() == BYTE || kind() == SHORT || kind() == INT || kind() == LONG;
    }

    public boolean isDecimal() {
        return kind() == FLOAT || kind() == DOUBLE;
    }

    private TypeKind kind() {
        return type.asType().getKind();
    }

    public boolean isString() {
        return type.getQualifiedName().contentEquals(String.class.getName());
    }
}
