package com.github.t1.exap;

public class AnnotationProcessingFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AnnotationProcessingFailedException() {
        super();
    }

    public AnnotationProcessingFailedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AnnotationProcessingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnnotationProcessingFailedException(String message) {
        super(message);
    }

    public AnnotationProcessingFailedException(Throwable cause) {
        super(cause);
    }
}
