package com.github.t1.exap.generator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static java.util.Arrays.asList;

public class JavaDocGenerator {
    private final String indent;
    private final String javaDoc;

    public JavaDocGenerator(String indent, String javaDoc) {
        this.indent = indent;
        this.javaDoc = javaDoc;
    }

    @Override public String toString() {
        var out = new StringWriter();
        print(new PrintWriter(out));
        return out.toString();
    }

    public void print(PrintWriter out) {
        List<String> lines = asList(javaDoc.split("\n"));
        if (lines.size() == 1) {
            out.append(indent).append("/** ").append(javaDoc).println(" */");
        } else {
            out.append(indent).append("/**\n");
            lines.forEach(line -> out.append(indent).append(" * ").append(line).append("\n"));
            out.append(indent).append(" */\n");
        }
    }

}
