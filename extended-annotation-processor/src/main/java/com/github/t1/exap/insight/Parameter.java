package com.github.t1.exap.insight;

import javax.lang.model.element.VariableElement;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class Parameter extends Elemental {
    private final VariableElement param;
    private final Method method;

    public Parameter(Method method, VariableElement param) {
        super(method.round());
        this.method = requireNonNull(method);
        this.param = requireNonNull(param);
    }

    @Override protected VariableElement getElement() {return param;}

    @Override public Optional<Elemental> enclosingElement() {return Optional.of(method);}

    public Method getMethod() {
        return method;
    }

    public String getName() {
        return param.getSimpleName().toString();
    }

    public Type getType() {
        return Type.of(param.asType(), round());
    }

    @Override public String toString() {return getType() + " " + getName();}
}
