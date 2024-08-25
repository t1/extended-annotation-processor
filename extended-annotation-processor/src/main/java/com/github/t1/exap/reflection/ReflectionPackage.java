package com.github.t1.exap.reflection;

import com.github.t1.exap.Round;
import com.github.t1.exap.insight.AnnotationWrapper;
import com.github.t1.exap.insight.Package;

import javax.lang.model.element.PackageElement;
import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Arrays.asList;

class ReflectionPackage extends Package {
    public static Package of(Class<?> type, Round round) {
        java.lang.Package pkg = type.getPackage();
        return (pkg == null) ? null : new ReflectionPackage(pkg, round);
    }

    private final java.lang.Package pkg;

    private ReflectionPackage(java.lang.Package pack, Round round) {
        super(ReflectionDummyProxy.of(PackageElement.class), round);
        this.pkg = pack;
    }

    @Override
    public String getName() {
        return (pkg == null) ? null : pkg.getName();
    }

    @Override
    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return asList(pkg.getAnnotationsByType(type));
    }

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return ReflectionAnnotationWrapper.allOn(pkg);
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return ReflectionAnnotationWrapper.ofTypeOn(pkg, type);
    }
}
