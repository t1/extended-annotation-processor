package com.github.t1.exap.reflection;

import javax.lang.model.element.PackageElement;

import com.github.t1.exap.Round;

public class ReflectionPackage extends Package {
    private final java.lang.Package pack;

    public ReflectionPackage(java.lang.Package pack, Round round) {
        super(DummyProxy.of(PackageElement.class), round);
        this.pack = pack;
    }

    @Override
    public String getName() {
        return (pack == null) ? null : pack.getName();
    }
}
