package com.github.t1.exap.reflection;

import static javax.lang.model.element.ElementKind.*;
import static javax.lang.model.type.TypeKind.*;

import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

public class Type extends Elemental {
    public static Type of(TypeMirror type, ProcessingEnvironment processingEnvironment) {
        TypeKind kind = type.getKind();
        switch (kind) {
            case BOOLEAN:
                return new ReflectionType(processingEnvironment, boolean.class);
            case BYTE:
                return new ReflectionType(processingEnvironment, byte.class);
            case CHAR:
                return new ReflectionType(processingEnvironment, char.class);
            case DOUBLE:
                return new ReflectionType(processingEnvironment, double.class);
            case FLOAT:
                return new ReflectionType(processingEnvironment, float.class);
            case INT:
                return new ReflectionType(processingEnvironment, int.class);
            case LONG:
                return new ReflectionType(processingEnvironment, long.class);
            case SHORT:
                return new ReflectionType(processingEnvironment, short.class);
            case VOID:
                return new ReflectionType(processingEnvironment, void.class);

            case ARRAY:
            case DECLARED:
                TypeElement typeElement = (TypeElement) ((DeclaredType) type).asElement();
                return new Type(processingEnvironment, typeElement);
            case ERROR:
                throw new RuntimeException("error parameter kind: " + kind + ": " + type);
            case EXECUTABLE:
            case INTERSECTION:
            case NONE:
            case NULL:
            case OTHER:
            case PACKAGE:
            case TYPEVAR:
            case UNION:
            case WILDCARD:
                throw new RuntimeException("unexpected parameter kind: " + kind + ": " + type);
        }
        throw new UnsupportedOperationException("unsupported parameter kind: " + kind + ": " + type);
    }

    private final TypeElement type;

    public Type(ProcessingEnvironment processingEnv, TypeElement type) {
        super(processingEnv, type);
        this.type = type;
    }

    private TypeKind typeKind() {
        return type.asType().getKind();
    }

    private ElementKind kind() {
        return type.getKind();
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
        return typeKind() == BOOLEAN;
    }

    public boolean isNumber() {
        return isInteger() || isDecimal();
    }

    public boolean isInteger() {
        return typeKind() == BYTE || typeKind() == SHORT || typeKind() == INT || typeKind() == LONG;
    }

    public boolean isDecimal() {
        return typeKind() == FLOAT || typeKind() == DOUBLE;
    }

    public boolean isString() {
        return type.getQualifiedName().contentEquals(String.class.getName());
    }

    public boolean isEnum() {
        return kind() == ENUM;
    }

    public List<String> getEnumValues() {
        if (!isEnum())
            return null;
        List<String> values = new ArrayList<>();
        for (Element element : type.getEnclosedElements())
            if (element.getKind() == ENUM_CONSTANT)
                values.add(element.getSimpleName().toString());
        return values;
    }
}
