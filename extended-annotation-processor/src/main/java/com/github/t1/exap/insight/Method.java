package com.github.t1.exap.insight;

import com.github.t1.exap.Round;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class Method extends Elemental implements Comparable<Method> {
    private static final Comparator<Method> COMPARATOR = Comparator.comparing(Method::name);

    private final Type declaringType;
    private final ExecutableElement method;

    public Method(Type declaringType, ExecutableElement method, Round round) {
        super(round);
        this.declaringType = requireNonNull(declaringType);
        this.method = requireNonNull(method);
    }

    @Override public ExecutableElement getElement() {return method;}

    @Override public Optional<Elemental> enclosingElement() {return Optional.of(declaringType);}

    public String name() {return method.getSimpleName().toString();}

    public List<Parameter> getParameters() {
        List<Parameter> result = new ArrayList<>();
        for (VariableElement param : method.getParameters())
            result.add(new Parameter(this, param));
        return result;
    }

    public Parameter getParameter(int index) {return new Parameter(this, method.getParameters().get(index));}

    public Type getDeclaringType() {return declaringType;}

    public Type getReturnType() {return Type.of(method.getReturnType(), round());}

    @Override public String toString() {
        return declaringType + "#" + name() + getParameters().stream()
                .map(Parameter::toString).collect(joining(", ", "(", ")"));
    }

    @Override public int compareTo(Method that) {return COMPARATOR.compare(this, that);}
}
