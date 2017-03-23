package com.github.t1.exap.reflection;

import javax.tools.FileObject;
import java.io.*;
import java.net.URI;
import java.nio.file.*;

public class Resource {
    private FileObject fileObject;

    public Resource(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    public URI getUri() {
        return fileObject.toUri();
    }

    public Path getPath() { return Paths.get(getUri()); }

    public String getName() {
        return fileObject.getName();
    }

    public Writer openWriter() throws IOException {
        return fileObject.openWriter();
    }
}
