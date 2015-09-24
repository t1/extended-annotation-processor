package com.github.t1.exap.reflection;

import javax.lang.model.element.VariableElement;

public class Parameter extends Elemental {
    private final VariableElement param;
    private final Method method;

    public Parameter(Method method, VariableElement param) {
        super(method.env(), param);
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
        return Type.of(param.asType(), env());
    }
}
