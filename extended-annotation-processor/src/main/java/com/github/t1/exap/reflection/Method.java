package com.github.t1.exap.reflection;

import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;

public class Method extends Elemental {
    private final Type containerType;
    private final ExecutableElement method;

    public Method(ProcessingEnvironment processingEnv, Type containerType, ExecutableElement method) {
        super(processingEnv, method);
        this.containerType = containerType;
        this.method = method;
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

    public Type getContainerType() {
        return containerType;
    }

    public Type getReturnType() {
        return Type.of(method.getReturnType(), env());
    }

    @Override
    public String toString() {
        return "Method:" + containerType.getFullName() + "#" + method.getSimpleName();
    }
}
