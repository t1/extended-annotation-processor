package com.github.t1.exap.reflection;

import java.util.Set;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.ABSTRACT;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isInterface;
import static java.lang.reflect.Modifier.isNative;
import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isStrict;
import static java.lang.reflect.Modifier.isSynchronized;
import static java.lang.reflect.Modifier.isTransient;
import static java.lang.reflect.Modifier.isVolatile;
import static java.util.stream.Collectors.toUnmodifiableSet;

class ReflectionModifiers {
    public static ReflectionModifiers on(int modifiers) {
        return new ReflectionModifiers(modifiers);
    }

    private final int modifiers;

    private ReflectionModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public boolean is(javax.lang.model.element.Modifier modifier) {
        return switch (modifier) {
            case PUBLIC -> isPublic(modifiers);
            case PROTECTED -> isProtected(modifiers);
            case PRIVATE -> isPrivate(modifiers);
            case ABSTRACT -> isAbstract(modifiers);
            case DEFAULT ->
                // Logic from java.lang.reflect.Method#isDefault:
                // Default methods are public non-abstract instance methods declared in an interface.
                    ((modifiers & (ABSTRACT | PUBLIC | STATIC)) == PUBLIC) && isInterface(modifiers);
            case STATIC -> isStatic(modifiers);
            // these two are not in the modifiers
            // #case SEALED ->
            // #case NON_SEALED ->
            case FINAL -> isFinal(modifiers);
            case TRANSIENT -> isTransient(modifiers);
            case VOLATILE -> isVolatile(modifiers);
            case SYNCHRONIZED -> isSynchronized(modifiers);
            case NATIVE -> isNative(modifiers);
            case STRICTFP -> isStrict(modifiers);
            default -> throw new UnsupportedOperationException("modifier " + modifier + " not yet supported");
        };
    }

    public Set<javax.lang.model.element.Modifier> toSet() {
        return Stream.of(javax.lang.model.element.Modifier.values())
                // see the comments in the switch above
                .filter(modifier -> !Set.of("SEALED", "NON_SEALED").contains(modifier.name()))
                .filter(this::is)
                .collect(toUnmodifiableSet());
    }
}
