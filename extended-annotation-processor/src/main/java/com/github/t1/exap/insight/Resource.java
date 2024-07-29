package com.github.t1.exap.insight;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Resource {
    private final FileObject fileObject;

    public Resource(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    public URI getUri() {
        return fileObject.toUri();
    }

    public Path getPath() {return Paths.get(getUri());}

    public String getName() {
        return fileObject.getName();
    }

    public Writer openWriter() throws IOException {
        return fileObject.openWriter();
    }
}
