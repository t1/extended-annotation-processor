package com.github.t1.exap.generator;

public class ParameterGenerator {
    private final String name;
    private TypeExpressionGenerator type;

    public ParameterGenerator(String name) {
        this.name = name;
    }

    public ParameterGenerator type(TypeExpressionGenerator type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public TypeExpressionGenerator getType() {
        return type;
    }
}
