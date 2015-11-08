package com.github.t1.exap.reflection;

import static javax.tools.StandardLocation.*;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.PackageElement;

import com.github.t1.exap.Round;
import com.github.t1.exap.generator.TypeGenerator;

public class Package {
    private final PackageElement packageElement;
    private Round round;

    public Package(PackageElement packageElement, Round round) {
        this.packageElement = packageElement;
        this.round = round;
    }

    public String getName() {
        return (packageElement == null) ? "" : packageElement.getQualifiedName().toString();
    }

    public Resource createSource(String relativeName) {
        try {
            return new Resource(filer().createSourceFile(getName() + "." + relativeName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    private Filer filer() {
        return round.env().getFiler();
    }

    public Resource createResource(String relativeName) {
        try {
            return new Resource(filer().createResource(CLASS_OUTPUT, getName(), relativeName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TypeGenerator openTypeGenerator(String name) {
        return new TypeGenerator(round.log(), this, name);
    }
}
