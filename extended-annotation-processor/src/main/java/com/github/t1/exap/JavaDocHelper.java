package com.github.t1.exap;

import java.util.function.Function;

class JavaDocHelper {
    /**
     * This can't be defined in the JavaDoc annotation directly, or accessing the class throws a
     * <code>java.lang.IllegalArgumentException: private static java.lang.String
     * com.github.t1.exap.JavaDoc.lambda$static$3(com.github.t1.exap.JavaDoc) has params at
     * sun.reflect.annotation.AnnotationType.<init>(AnnotationType.java:122)</code>
     */
    static final Function<JavaDoc, String> JAVADOC_SUMMARY = (d) -> {
        String value = d.value();
        int firstDot = value.indexOf('.');
        return (firstDot < 0) ? value : value.substring(0, firstDot);
    };
}
