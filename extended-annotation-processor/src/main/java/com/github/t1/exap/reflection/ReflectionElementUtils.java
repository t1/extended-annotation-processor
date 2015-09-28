package com.github.t1.exap.reflection;

import java.io.Writer;
import java.util.*;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;

class ReflectionElementUtils implements Elements {

    @Override
    public PackageElement getPackageElement(CharSequence name) {
        return null;
    }

    @Override
    public TypeElement getTypeElement(CharSequence name) {
        return null;
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(
            AnnotationMirror a) {
        return null;
    }

    @Override
    public String getDocComment(Element e) {
        return null;
    }

    @Override
    public boolean isDeprecated(Element e) {
        return false;
    }

    @Override
    public Name getBinaryName(TypeElement type) {
        return null;
    }

    @Override
    public PackageElement getPackageOf(Element type) {
        return null;
    }

    @Override
    public List<? extends Element> getAllMembers(TypeElement type) {
        return null;
    }

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
        return null;
    }

    @Override
    public boolean hides(Element hider, Element hidden) {
        return false;
    }

    @Override
    public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
        return false;
    }

    @Override
    public String getConstantExpression(Object value) {
        return null;
    }

    @Override
    public void printElements(Writer w, Element... elements) {

    }

    @Override
    public Name getName(CharSequence cs) {
        return null;
    }

    @Override
    public boolean isFunctionalInterface(TypeElement type) {
        return false;
    }
}
