package com.github.t1.exap;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

import com.github.t1.exap.reflection.ReflectionType;

/**
 * An annotation processor can access the javadoc, so we could provide this for all types. But it also should be
 * possible to test your annotation processors with the {@link ReflectionType} and friends. So we don't provide an extra
 * api for javadoc, but simulate it with this annotation. Of course, this also allow you to use this annotation instead
 * of the 'real' javadoc... and it works even for parameters, which are not supported by the java compiler.
 */
@Retention(RUNTIME)
public @interface JavaDoc {
    /**
     * The first sentence of the JavaDoc is often treated as the summary of the rest of the text, which works
     * astonishingly well. This is this first sentence, without the period.
     */
    String summary();

    /**
     * This is the main body of the JavaDoc text, *including* the {@link #summary() first line}. You should not rely on
     * any markup: even the commonly used more-or-less html may, in a future version, be converted to, e.g., Markdown.
     */
    String value();
}
