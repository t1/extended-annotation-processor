package com.github.t1.exap;

import java.lang.annotation.Retention;
import java.util.function.Function;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation processor can access the javadoc, so we could provide this for all types. But it also should be
 * possible to unit test your annotation processors using reflection. So we don't provide an extra api for javadoc, but
 * simulate it with this annotation. Of course, this also allow you to use this annotation instead of the 'real'
 * javadoc... and it works even for parameters, which are not supported by the java compiler.
 * <p>
 * <em>Note:</em> The annotation processor provides the javadoc only for the classes currently being compiled. If you
 * reference classes already compiled, e.g. from a library, they don't have javadoc attached. You can then, too, resort to
 * annotating those classes with this.
 */
@Retention(RUNTIME)
public @interface JavaDoc {
    /**
     * The first sentence of the JavaDoc is often treated as the summary of the rest of the text, which works
     * astonishingly well. This is this first sentence, without the period.
     */
    Function<JavaDoc, String> SUMMARY = JavaDocHelper.JAVADOC_SUMMARY;

    /**
     * The full JavaDoc text. To get a more useful subset, you should consider one of the extractor methods defined in
     * {@link JavaDoc}.
     */
    String value();
}
