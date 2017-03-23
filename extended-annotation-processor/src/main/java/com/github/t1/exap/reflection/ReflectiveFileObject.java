package com.github.t1.exap.reflection;

import org.slf4j.*;

import javax.tools.FileObject;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.Files.*;
import static java.util.Objects.*;

public class ReflectiveFileObject implements FileObject {
    private static final Logger log = LoggerFactory.getLogger(ReflectiveFileObject.class);

    private final Path path;

    public ReflectiveFileObject(Path path) { this.path = requireNonNull(path); }

    @Override public URI toUri() { return path.toUri(); }

    @Override public String getName() { return path.getFileName().toString(); }

    @Override public InputStream openInputStream() throws IOException { return newInputStream(path); }

    @Override public OutputStream openOutputStream() throws IOException {
        createParentDir();
        return newOutputStream(path);
    }

    @Override public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return newBufferedReader(path);
    }

    @Override public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return new String(readAllBytes(path), UTF_8);
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

    private void createParentDir() throws IOException { createDirectories(path.getParent()); }
}
