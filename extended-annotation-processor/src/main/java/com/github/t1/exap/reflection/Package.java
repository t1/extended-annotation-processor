package com.github.t1.exap.reflection;

import static javax.tools.StandardLocation.*;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;

import com.github.t1.exap.generator.TypeGenerator;

public class Package {
    private ProcessingEnvironment processingEnv;
    private PackageElement packageElement;

    public Package(ProcessingEnvironment processingEnv, PackageElement packageElement) {
        this.processingEnv = processingEnv;
        this.packageElement = packageElement;
    }

    public String getName() {
        return (packageElement == null) ? "" : packageElement.getQualifiedName().toString();
    }

    public Resource createSource(String relativeName) {
        try {
            return new Resource(processingEnv.getFiler().createSourceFile(getName() + "." + relativeName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource createResource(String relativeName) {
        try {
            return new Resource(processingEnv.getFiler().createResource(CLASS_OUTPUT, getName(), relativeName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TypeGenerator openTypeGenerator(String name) {
        return new TypeGenerator(this, name);
    }
}
