package com.github.t1.exap.reflection;

import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.*;

public class ReflectionProcessingEnvironment implements ProcessingEnvironment {
    private final ReflectionMessager messager = new ReflectionMessager();
    private final Map<Class<?>, ReflectionType> types = new HashMap<>();

    public ReflectionType type(Class<?> type) {
        ReflectionType reflectionType = types.get(type);
        if (reflectionType == null) {
            reflectionType = new ReflectionType(this, type);
            types.put(type, reflectionType);
        }
        return reflectionType;
    }

    @Override
    public Map<String, String> getOptions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReflectionMessager getMessager() {
        return messager;
    }

    @Override
    public Filer getFiler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Elements getElementUtils() {
        return new ReflectionElementUtils();
    }

    @Override
    public Types getTypeUtils() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SourceVersion getSourceVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }
}
