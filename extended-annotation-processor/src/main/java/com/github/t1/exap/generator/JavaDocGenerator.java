package com.github.t1.exap.generator;

import static java.util.Arrays.*;

import java.io.PrintWriter;
import java.util.List;

public class JavaDocGenerator {
    private String indent;
    private String javaDoc;

    public JavaDocGenerator(String indent, String javaDoc) {
        this.indent = indent;
        this.javaDoc = javaDoc;
    }

    public void print(PrintWriter out) {
        List<String> lines = asList(javaDoc.split("\n"));
        if (lines.size() == 1) {
            out.append(indent).append("/** ").append(javaDoc).println(" */");
        } else {
            out.append(indent).append("/**\n");
            lines.stream().forEach(line -> out.append(indent).append(" * ").append(line).append("\n"));
            out.append(indent).append(" */\n");
        }
    }

}
