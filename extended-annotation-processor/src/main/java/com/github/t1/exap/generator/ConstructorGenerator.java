package com.github.t1.exap.generator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ConstructorGenerator {
    private final TypeGenerator container;
    private List<ParameterGenerator> parameters;
    private String body;

    public ConstructorGenerator(TypeGenerator container) {
        this.container = container;
    }

    @Override public String toString() {
        var out = new StringWriter();
        print(new PrintWriter(out));
        return out.toString();
    }

    public ParameterGenerator addParameter(String name) {
        if (this.parameters == null)
            this.parameters = new ArrayList<>();
        ParameterGenerator parameterGenerator = new ParameterGenerator(container, name);
        this.parameters.add(parameterGenerator);
        return parameterGenerator;
    }

    public ConstructorGenerator body(String body) {
        this.body = body;
        return this;
    }

    public void print(PrintWriter out) {
        out.println();
        out.print("    public " + container.getTypeName() + "(");
        ParameterGenerator.print(parameters, out);
        out.println(") {");
        out.println("        " + body);
        out.println("    }");
    }
}
