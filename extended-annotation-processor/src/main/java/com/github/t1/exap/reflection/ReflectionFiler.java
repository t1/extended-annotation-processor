package com.github.t1.exap.reflection;

import static javax.tools.StandardLocation.*;

import java.util.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.*;
import javax.tools.JavaFileManager.Location;

public class ReflectionFiler implements Filer {
    private final List<ReflectionFileObject> list = new ArrayList<>();

    @Override
    public JavaFileObject createSourceFile(CharSequence name, Element... originatingElements) {
        int i = name.toString().lastIndexOf('.');
        CharSequence pkg = (i < 0) ? "" : name.subSequence(0, i);
        CharSequence relativeName = (i < 0) ? name : name.subSequence(i + 1, name.length());
        return (JavaFileObject) createResource(SOURCE_OUTPUT, pkg, relativeName, originatingElements);
    }

    @Override
    public JavaFileObject createClassFile(CharSequence name, Element... originatingElements) {
        int i = name.toString().lastIndexOf('.');
        CharSequence pkg = (i < 0) ? "" : name.subSequence(0, i);
        CharSequence relativeName = (i < 0) ? name : name.subSequence(i, name.length());
        return (JavaFileObject) createResource(CLASS_OUTPUT, pkg, relativeName, originatingElements);
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
