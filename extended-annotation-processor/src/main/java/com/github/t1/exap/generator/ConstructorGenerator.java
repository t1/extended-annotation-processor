package com.github.t1.exap.generator;

import java.io.PrintWriter;
import java.util.*;

public class ConstructorGenerator {
    private final TypeGenerator container;
    private List<ParameterGenerator> parameters;
    private String body;

    public ConstructorGenerator(TypeGenerator container) {
        this.container = container;
    }

    public ParameterGenerator addParameter(String name) {
        ParameterGenerator parameterGenerator = new ParameterGenerator(name);
        if (this.parameters == null)
            this.parameters = new ArrayList<>();
        this.parameters.add(parameterGenerator);
        return parameterGenerator;
    }

    public ConstructorGenerator body(String body) {
        this.body = body;
        return this;
    }

    public void print(PrintWriter out) {
        out.print("    public " + container.getTypeName() + "(");
        if (parameters != null) {
            Delimiter delimiter = new Delimiter(", ");
            for (ParameterGenerator param : parameters)
                out.append(delimiter.next()).append(param.getType().toString()) //
                        .append(" ").append(param.getName());
        }
        out.println(") {");
        out.println("        " + body);
        out.println("    }");
        out.println();
    }
}
