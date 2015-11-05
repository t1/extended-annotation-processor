package com.github.t1.exap.reflection;

import static java.util.Objects.*;

import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;

public class Method extends Elemental {
    private final Type declaringType;
    private final ExecutableElement method;

    public Method(ProcessingEnvironment processingEnv, Type declaringType, ExecutableElement method) {
        super(processingEnv);
        this.declaringType = requireNonNull(declaringType);
        this.method = requireNonNull(method);
    }

    @Override
    protected ExecutableElement getElement() {
        return method;
    }

    public String getName() {
        return method.getSimpleName().toString();
    }

    public List<Parameter> getParameters() {
        List<Parameter> result = new ArrayList<>();
        for (VariableElement param : method.getParameters())
            result.add(new Parameter(this, param));
        return result;
    }

    public Parameter getParameter(int index) {
        return new Parameter(this, method.getParameters().get(index));
    }

    public Type getDeclaringType() {
        return declaringType;
    }

    public Type getReturnType() {
        return Type.of(method.getReturnType(), env());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getDeclaringType().getSimpleName() + "#" + getName();
    }
}
