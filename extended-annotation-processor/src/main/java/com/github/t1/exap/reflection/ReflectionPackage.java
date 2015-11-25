package com.github.t1.exap.reflection;

import static java.util.Arrays.*;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.PackageElement;

import com.github.t1.exap.Round;

public class ReflectionPackage extends Package {
    private final java.lang.Package pkg;

    public ReflectionPackage(java.lang.Package pack, Round round) {
        super(DummyProxy.of(PackageElement.class), round);
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
        return ReflectionAnnotationWrapper.allOn(pkg, round());
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return ReflectionAnnotationWrapper.ofTypeOn(pkg, type, round());
    }
}
