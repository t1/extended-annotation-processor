package com.github.t1.exap.reflection;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;

class ReflectionJavaFileObject implements JavaFileObject {
    final Location location;
    final CharSequence pkg;
    final CharSequence relativeName;

    private final StringWriter content = new StringWriter();

    public ReflectionJavaFileObject(Location location, CharSequence pkg, CharSequence relativeName) {
        this.location = location;
        this.pkg = pkg;
        this.relativeName = relativeName;
    }

    @Override
    public URI toUri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return pkg + "." + relativeName;
    }

    @Override
    public InputStream openInputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream openOutputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return content.toString();
    }

    @Override
    public Writer openWriter() {
        return content;
    }

    @Override
    public long getLastModified() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Kind getKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return location + ":" + pkg + ":" + relativeName + ":[" + content + "]";
    }
}
