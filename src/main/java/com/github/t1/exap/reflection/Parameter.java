package com.github.t1.exap.reflection;

import javax.lang.model.element.*;
import javax.lang.model.type.*;

public class Parameter extends Elemental {
    private final VariableElement param;
    private final Method method;

    public Parameter(Method method, VariableElement param) {
        super(method.getProcessingEnv(), param);
        this.method = method;
        this.param = param;
    }

    public Method getMethod() {
        return method;
    }

    public String getName() {
        return param.getSimpleName().toString();
    }

    @Override
    public String toString() {
        return "Parameter:" + getMethod().getSimpleName() + "#" + getName();
    }

    public Type getType() {
        TypeMirror type = param.asType();
        TypeKind kind = type.getKind();
        switch (kind) {
            case BOOLEAN:
                return new ReflectionType(getProcessingEnv(), boolean.class);
            case BYTE:
                return new ReflectionType(getProcessingEnv(), byte.class);
            case CHAR:
                return new ReflectionType(getProcessingEnv(), char.class);
            case DOUBLE:
                return new ReflectionType(getProcessingEnv(), double.class);
            case FLOAT:
                return new ReflectionType(getProcessingEnv(), float.class);
            case INT:
                return new ReflectionType(getProcessingEnv(), int.class);
            case LONG:
                return new ReflectionType(getProcessingEnv(), long.class);
            case SHORT:
                return new ReflectionType(getProcessingEnv(), short.class);

            case ARRAY:
            case VOID:
            case DECLARED:
                TypeElement typeElement = (TypeElement) ((DeclaredType) type).asElement();
                return new Type(getProcessingEnv(), typeElement);
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
}
