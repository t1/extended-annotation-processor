package com.github.t1.exap.reflection;

import java.lang.reflect.Modifier;

class ReflectionModifiers {
    public static ReflectionModifiers on(int modifiers) {
        return new ReflectionModifiers(modifiers);
    }

    private final int modifiers;

    private ReflectionModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public boolean is(javax.lang.model.element.Modifier modifier) {
        switch (modifier) {
            case ABSTRACT:
                return Modifier.isAbstract(modifiers);
            case DEFAULT:
                throw new UnsupportedOperationException("not a java.lang.reflect.Modifier");
            case FINAL:
                return Modifier.isFinal(modifiers);
            case NATIVE:
                return Modifier.isNative(modifiers);
            case PRIVATE:
                return Modifier.isPrivate(modifiers);
            case PROTECTED:
                return Modifier.isProtected(modifiers);
            case PUBLIC:
                return Modifier.isPublic(modifiers);
            case STATIC:
                return Modifier.isStatic(modifiers);
            case STRICTFP:
                return Modifier.isStrict(modifiers);
            case SYNCHRONIZED:
                return Modifier.isSynchronized(modifiers);
            case TRANSIENT:
                return Modifier.isTransient(modifiers);
            case VOLATILE:
                return Modifier.isVolatile(modifiers);
        }
        throw new UnsupportedOperationException("not yet supported");
    }
}
