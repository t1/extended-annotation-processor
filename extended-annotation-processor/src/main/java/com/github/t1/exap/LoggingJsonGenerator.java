package com.github.t1.exap;

import java.math.*;
import java.util.Stack;

import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import org.slf4j.Logger;

public class LoggingJsonGenerator implements JsonGenerator {
    public static JsonGenerator of(Logger log, JsonGenerator delegate) {
        return new LoggingJsonGenerator(log, delegate);
    }

    private final Logger log;
    private final JsonGenerator delegate;

    private int lineNumber = 2; // the JsonGenerator starts with an empty line
    private String prefix = "-> ";
    private final Stack<Boolean> isObjectStack = new Stack<>();

    public LoggingJsonGenerator(Logger log, JsonGenerator delegate) {
        this.log = log;
        this.delegate = delegate;
    }

    private void log(String message, Object... args) {
        log.debug(lineNumber++ + ":" + prefix + message, args);
    }

    @Override
    public JsonGenerator writeStartObject() {
        log("{");
        indent(true);
        delegate.writeStartObject();
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        log("{}:{", name);
        indent(true);
        delegate.writeStartObject(name);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        log("[");
        indent(false);
        delegate.writeStartArray();
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        log("{}:[", name);
        indent(false);
        delegate.writeStartArray(name);
        return this;
    }

    private void indent(boolean isObject) {
        this.isObjectStack.push(isObject);
        prefix += "    ";
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        log("{}:{}", name, value);
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, String value) {
        log("{}:{}", name, value);
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        log("{}:{}", name, value);
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        log("{}:{}", name, value);
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, int value) {
        log("{}:{}", name, value);
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, long value) {
        log("{}:{}", name, value);
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, double value) {
        log("{}:{}", name, value);
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        log("{}:{}", name, value);
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator writeNull(String name) {
        log("{}:null", name);
        delegate.writeNull(name);
        return this;
    }

    @Override
    public JsonGenerator writeEnd() {
        outdent();
        log((isObjectStack.pop() ? "}" : "]"));
        delegate.writeEnd();
        return this;
    }

    private void outdent() {
        prefix = prefix.substring(0, prefix.length() - 4);
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        log("{}", value);
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String value) {
        log("{}", value);
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        log("{}", value);
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        log("{}", value);
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(int value) {
        log("{}", value);
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(long value) {
        log("{}", value);
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(double value) {
        log("{}", value);
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(boolean value) {
        log("{}", value);
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator writeNull() {
        log("null");
        delegate.writeNull();
        return this;
    }

    @Override
    public void flush() {
        log("--- flush");
        delegate.flush();
    }

    @Override
    public void close() {
        log("--- close");
        delegate.close();
    }
}
