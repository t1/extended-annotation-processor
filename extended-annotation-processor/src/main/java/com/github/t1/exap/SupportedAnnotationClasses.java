package com.github.t1.exap;

import javax.annotation.processing.SupportedAnnotationTypes;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Similar to {@link SupportedAnnotationTypes}, but with class names instead of String constants... which is better for
 * refactoring, etc.
 */
@Inherited
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface SupportedAnnotationClasses {
    Class<? extends Annotation>[] value();
}
