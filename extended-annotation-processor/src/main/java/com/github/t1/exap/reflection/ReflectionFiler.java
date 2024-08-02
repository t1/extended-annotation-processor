package com.github.t1.exap.reflection;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

class ReflectionFiler implements Filer {
    private final List<ReflectionJavaFileObject> list = new ArrayList<>();

    @Override
    public JavaFileObject createSourceFile(CharSequence name, Element... originatingElements) {
        var i = name.toString().lastIndexOf('.');
        var pkg = (i < 0) ? "" : name.subSequence(0, i);
        var relativeName = (i < 0) ? name : name.subSequence(i + 1, name.length());
        return (JavaFileObject) createResource(SOURCE_OUTPUT, pkg, relativeName, originatingElements);
    }

    @Override
    public JavaFileObject createClassFile(CharSequence name, Element... originatingElements) {
        var i = name.toString().lastIndexOf('.');
        var pkg = (i < 0) ? "" : name.subSequence(0, i);
        var relativeName = (i < 0) ? name : name.subSequence(i, name.length());
        return (JavaFileObject) createResource(CLASS_OUTPUT, pkg, relativeName, originatingElements);
    }

    @Override
    public FileObject createResource(Location location, CharSequence pkg, CharSequence relativeName,
                                     Element... originatingElements) {
        var file = new ReflectionJavaFileObject(location, pkg, relativeName);
        list.add(file);
        return file;
    }

    @Override
    public FileObject getResource(Location location, CharSequence pkg, CharSequence relativeName) {
        return list.stream()
                .filter(file -> file.location == location && file.pkg.equals(pkg) && file.relativeName.equals(relativeName))
                .findFirst().orElse(null);
    }

    public String getCreatedResource(StandardLocation location, String pkg, String relativeName) {
        var resource = getResource(location, pkg, relativeName);
        try {
            return (resource == null) ? null : resource.getCharContent(true).toString();
        } catch (IOException e) {
            throw new RuntimeException("cannot read content of " + location + ": " + pkg + "/" + relativeName, e);
        }
    }

    public List<? extends JavaFileObject> getCreatedResources() {
        return list;
    }
}
