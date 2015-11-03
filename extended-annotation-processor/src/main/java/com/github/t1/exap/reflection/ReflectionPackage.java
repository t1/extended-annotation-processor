package com.github.t1.exap.reflection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;

public class ReflectionPackage extends Package {
    private final java.lang.Package pack;

    public ReflectionPackage(ProcessingEnvironment processingEnv, java.lang.Package pack) {
        super(processingEnv, DummyProxy.of(PackageElement.class));
        this.pack = pack;
    }

    @Override
    public String getName() {
        return pack.getName();
    }
}
