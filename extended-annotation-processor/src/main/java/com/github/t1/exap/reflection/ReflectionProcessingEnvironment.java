package com.github.t1.exap.reflection;

import com.github.t1.exap.Round;
import org.slf4j.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.*;
import javax.tools.*;
import javax.tools.Diagnostic.Kind;
import java.util.*;

public class ReflectionProcessingEnvironment implements ProcessingEnvironment {
    private static final Logger log = LoggerFactory.getLogger(ReflectionProcessingEnvironment.class);

    public static final ReflectionProcessingEnvironment ENV = new ReflectionProcessingEnvironment();

    private static final Round DUMMY_ROUND = new Round(log, ENV, null, 0);

    private ReflectionProcessingEnvironment() {}

    private final ReflectionMessager messager = new ReflectionMessager();
    private final ReflectionFiler filer = new ReflectionFiler();
    private final ReflectionTypes types = new ReflectionTypes();

    @Override
    public Map<String, String> getOptions() {
        return null;
    }

    @Override
    public ReflectionMessager getMessager() {
        return messager;
    }

    @Override
    public Filer getFiler() {
        return filer;
    }

    @Override
    public Elements getElementUtils() {
        return new ReflectionElementUtils();
    }

    @Override
    public Types getTypeUtils() {
        return types;
    }

    @Override
    public SourceVersion getSourceVersion() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    void message(Elemental elemental, Diagnostic.Kind kind, CharSequence message) {
        messager.message(elemental, kind, message);
    }

    public Type type(Class<?> type) { return ReflectionType.type(type, DUMMY_ROUND); }

    public Type type(TypeMirror type) { return Type.of(type, DUMMY_ROUND); }

    public List<Message> getMessages() {
        return messager.getMessages();
    }

    public List<String> getMessages(Elemental target, Kind messageKind) {
        return messager.getMessages(target, messageKind);
    }

    public List<ReflectionFileObject> getCreatedResources() {
        return filer.getCreatedResources();
    }

    public String getCreatedResource(StandardLocation location, String pack, String name) {
        return filer.getCreatedResource(location, pack, name);
    }

    public void clearCreatedResource() {
        filer.getCreatedResources().clear();
    }
}
