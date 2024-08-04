package com.github.t1.exap.insight;

public class SourceAlreadyExistsException extends RuntimeException {
    private final String sourceName;

    public SourceAlreadyExistsException(String sourceName) {
        super("file already exists: " + sourceName);
        this.sourceName = sourceName;
    }

    public String getSourceName() {return sourceName;}
}
