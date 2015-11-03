package com.github.t1.exap.reflection;

import java.util.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.*;
import javax.tools.JavaFileManager.Location;

public class ReflectionFiler implements Filer {
    private final List<ReflectionFileObject> list = new ArrayList<>();

    @Override
    public JavaFileObject createSourceFile(CharSequence name, Element... originatingElements) {
        throw new UnsupportedOperationException("use the methods in " + Package.class);
    }

    @Override
    public JavaFileObject createClassFile(CharSequence name, Element... originatingElements) {
        throw new UnsupportedOperationException("use the methods in " + Package.class);
    }

    @Override
    public FileObject createResource(Location location, CharSequence pkg, CharSequence relativeName,
            Element... originatingElements) {
        ReflectionFileObject file = new ReflectionFileObject(location, pkg, relativeName);
        list.add(file);
        return file;
    }

    @Override
    public ReflectionFileObject getResource(Location location, CharSequence pkg, CharSequence relativeName) {
        for (ReflectionFileObject file : list)
            if (file.location == location && file.pkg.equals(pkg) && file.relativeName.equals(relativeName))
                return file;
        return null;
    }

    public String getCreatedResource(StandardLocation location, String pkg, String relativeName) {
        ReflectionFileObject resource = getResource(location, pkg, relativeName);
        return (resource == null) ? null : resource.getCharContent(true).toString();
    }

    public List<ReflectionFileObject> getCreatedResources() {
        return list;
    }
}
