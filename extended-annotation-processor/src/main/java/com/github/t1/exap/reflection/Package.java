package com.github.t1.exap.reflection;

import com.github.t1.exap.Round;
import com.github.t1.exap.generator.TypeGenerator;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

public class Package extends Elemental {
    private final PackageElement packageElement;
    private final Round round;

    public Package(PackageElement packageElement, Round round) {
        super(round);
        this.packageElement = packageElement;
        this.round = round;
    }

    @Override
    protected Element getElement() {
        return packageElement;
    }

    public String getName() {
        return (packageElement == null) ? "" : packageElement.getQualifiedName().toString();
    }

    public boolean isRoot() {
        return packageElement == null;
    }

    public boolean isSuperPackageOf(Package that) {
        return toPath().startsWith(that.toPath());
    }

    public Path toPath() {
        return Paths.get(toString());
    }

    public Resource createSource(String relativeName) {
        try {
            String sourceName = sourceName(relativeName);
            return new Resource(filer().createSourceFile(sourceName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String sourceName(String relativeName) {
        String sourceName = getName();
        if (!sourceName.isEmpty())
            sourceName += ".";
        sourceName += relativeName;
        return sourceName;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Package))
            return false;
        Package that = (Package) obj;
        return this.toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
