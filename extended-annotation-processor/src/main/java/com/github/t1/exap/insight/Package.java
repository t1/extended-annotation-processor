package com.github.t1.exap.insight;

import com.github.t1.exap.Round;
import com.github.t1.exap.generator.TypeGenerator;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

public class Package extends Elemental {
    private final PackageElement packageElement;
    private final Round round;

    public Package(PackageElement packageElement, Round round) {
        super(round);
        this.packageElement = packageElement;
        this.round = round;
    }

    @Override protected Element getElement() {return packageElement;}

    @Override public Optional<Elemental> enclosingElement() {
        if (isRoot()) return Optional.empty(); // TODO return the module
        var name = getName();
        var parentPackage = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : "";
        return Optional.of(round.getPackage(parentPackage));
    }

    public String getName() {
        return (packageElement == null) ? "" : packageElement.getQualifiedName().toString();
    }

    public boolean isRoot() {return packageElement == null;}

    public boolean isSuperPackageOf(Package that) {
        return toPath().startsWith(that.toPath());
    }

    public Path toPath() {return Paths.get(getName().replace('.', '/'));}

    public Resource createSource(String relativeName) {
            String sourceName = sourceName(relativeName);
        try {
            return new Resource(filer().createSourceFile(sourceName));
        } catch (FilerException e) {
            if (e.getMessage().contains("Attempt to recreate a file for type")) {
                throw new SourceAlreadyExistsException(sourceName);
            } else {
                throw new RuntimeException(e);
            }
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
        return new TypeGenerator(round, this, name);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getName();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Package that) && this.toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
