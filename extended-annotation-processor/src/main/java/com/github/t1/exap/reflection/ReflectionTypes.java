package com.github.t1.exap.reflection;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;
import java.util.List;

public class ReflectionTypes implements Types {
    @Override public Element asElement(TypeMirror t) {return ReflectionProcessingEnvironment.ENV.element(t);}

    @Override public boolean isSameType(TypeMirror t1, TypeMirror t2) {
        return false;
    }

    @Override public boolean isSubtype(TypeMirror t1, TypeMirror t2) {
        return false;
    }

    @Override public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
        return false;
    }

    @Override public boolean contains(TypeMirror t1, TypeMirror t2) {
        return false;
    }

    @Override public boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
        return false;
    }

    @Override public List<? extends TypeMirror> directSupertypes(TypeMirror t) {
        return null;
    }

    @Override public TypeMirror erasure(TypeMirror t) {
        return null;
    }

    @Override public TypeElement boxedClass(PrimitiveType p) {
        return null;
    }

    @Override public PrimitiveType unboxedType(TypeMirror t) {
        return null;
    }

    @Override public TypeMirror capture(TypeMirror t) {
        return null;
    }

    @Override public PrimitiveType getPrimitiveType(TypeKind kind) {
        return null;
    }

    @Override public NullType getNullType() {
        return null;
    }

    @Override public NoType getNoType(TypeKind kind) {
        return null;
    }

    @Override public ArrayType getArrayType(TypeMirror componentType) {
        return null;
    }

    @Override public WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound) {
        return null;
    }

    @Override public DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs) {
        return null;
    }

    @Override
    public DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem, TypeMirror... typeArgs) {
        return null;
    }

    @Override public TypeMirror asMemberOf(DeclaredType containing, Element element) {
        return null;
    }
}
