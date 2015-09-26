package com.github.t1.exap.reflection;

import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.*;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

public class ReflectionProcessingEnvironment implements ProcessingEnvironment {
    public static final ReflectionProcessingEnvironment ENV = new ReflectionProcessingEnvironment();

    private ReflectionProcessingEnvironment() {}

    private final ReflectionMessager messager = new ReflectionMessager();

    public List<Message> getMessages() {
        return messager.getMessages();
    }

    public List<String> getMessages(Elemental target, Kind messageKind) {
        return messager.getMessages(target, messageKind);
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

    public void message(Elemental elemental, Diagnostic.Kind kind, CharSequence message) {
        messager.message(elemental, kind, message);
    }
}
