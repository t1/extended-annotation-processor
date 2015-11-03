package com.github.t1.exap.reflection;

import java.io.*;
import java.net.URI;

import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;

class ReflectionFileObject implements FileObject {
    final Location location;
    final CharSequence pkg;
    final CharSequence relativeName;

    private final StringWriter content = new StringWriter();

    public ReflectionFileObject(Location location, CharSequence pkg, CharSequence relativeName) {
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
        throw new UnsupportedOperationException();
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
    public String toString() {
        return location + ":" + pkg + ":" + relativeName + ":[" + content + "]";
    }
}
