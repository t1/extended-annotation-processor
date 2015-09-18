package com.github.t1.exap.reflection;

import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;

public class Method extends Elemental {
    private final Type type;
    private final ExecutableElement method;

    public Method(ProcessingEnvironment processingEnv, Type type, ExecutableElement method) {
        super(processingEnv, method);
        this.type = type;
        this.method = method;
    }

    public String getSimpleName() {
        return method.getSimpleName().toString();
    }

    public List<Parameter> getParameters() {
        List<Parameter> result = new ArrayList<>();
        for (VariableElement param : method.getParameters()) {
            result.add(new Parameter(this, param));
        }
        return result;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Method:" + type.getQualifiedName() + "#" + method.getSimpleName();
    }

    public Type getReturnType() {
        return Type.of(method.getReturnType(), getProcessingEnv());
    }
}
