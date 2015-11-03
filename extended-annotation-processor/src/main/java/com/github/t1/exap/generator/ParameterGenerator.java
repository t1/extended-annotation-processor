package com.github.t1.exap.generator;

public class ParameterGenerator {
    private final String name;
    private TypeStringGenerator type;

    public ParameterGenerator(String name) {
        this.name = name;
    }

    public ParameterGenerator type(TypeStringGenerator type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public TypeStringGenerator getType() {
        return type;
    }
}
