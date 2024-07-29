package com.github.t1.exap.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.util.Objects.requireNonNull;

public class ReflectionFileObject implements FileObject {
    private static final Logger log = LoggerFactory.getLogger(ReflectionFileObject.class);

    private final Path path;

    public ReflectionFileObject(Path path) {this.path = requireNonNull(path);}

    @Override public URI toUri() {return path.toUri();}

    @Override public String getName() {return path.getFileName().toString();}

    @Override public InputStream openInputStream() throws IOException {return newInputStream(path);}

    @Override public OutputStream openOutputStream() throws IOException {
        createParentDir();
        return newOutputStream(path);
    }

    @Override public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return newBufferedReader(path);
    }

    @Override public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return Files.readString(path);
    }

    @Override public Writer openWriter() throws IOException {
        createParentDir();
        return newBufferedWriter(path);
    }

    @Override public long getLastModified() {
        try {
            return getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            log.debug("coult not get last modified time of " + path, e);
            return 0;
        }
    }

    @Override public boolean delete() {
        try {
            return deleteIfExists(path);
        } catch (IOException e) {
            log.debug("could not delete " + path, e);
            return false;
        }
    }

    private void createParentDir() throws IOException {createDirectories(path.getParent());}
}
