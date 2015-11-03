package com.github.t1.exap.reflection;

import java.io.*;

import javax.tools.FileObject;

public class Resource {
    private FileObject fileObject;

    public Resource(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    public String getName() {
        return fileObject.getName();
    }

    public Writer openWriter() throws IOException {
        return fileObject.openWriter();
    }
}
