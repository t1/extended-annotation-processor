package com.github.t1.exap.reflection;

import com.github.t1.exap.Round;
import com.github.t1.exap.insight.Elemental;
import com.github.t1.exap.insight.Message;
import com.github.t1.exap.insight.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionProcessingEnvironment implements ProcessingEnvironment {
    private static final Logger log = LoggerFactory.getLogger(ReflectionProcessingEnvironment.class);

    public static final ReflectionProcessingEnvironment ENV = new ReflectionProcessingEnvironment();

    private static final Round DUMMY_ROUND = new Round(log, ENV, null, 0);

    private ReflectionProcessingEnvironment() {}

    private final ReflectionMessager messager = new ReflectionMessager();
    private final ReflectionFiler filer = new ReflectionFiler();
    private final ReflectionTypes types = new ReflectionTypes();
    private final Map<String, String> javadocMocks = new ConcurrentHashMap<>();

    @Override
    public Map<String, String> getOptions() {
        return null;
    }

    @Override
    public Messager getMessager() {return messager;}

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

    public Type type(Class<?> type) {return ReflectionType.type(type, DUMMY_ROUND);}

    public Type type(TypeMirror type) {return Type.of(type, DUMMY_ROUND);}

    public Element element(TypeMirror type) {return new ReflectionTypeElement(type(type), DUMMY_ROUND);}

    public List<Message> getMessages() {
        return messager.getMessages();
    }

    public List<String> getMessages(Elemental target, Kind messageKind) {
        return messager.getMessages(target, messageKind);
    }

    List<? extends JavaFileObject> getCreatedResources() {
        return filer.getCreatedResources();
    }

    public String getCreatedResource(StandardLocation location, String pack, String name) {
        return filer.getCreatedResource(location, pack, name);
    }

    public void clearCreatedResources() {
        filer.getCreatedResources().clear();
    }

    public void mockJavadoc(AnnotatedElement annotatedElement, String javadoc) {
        javadocMocks.put(annotatedElement.toString(), javadoc);
    }

    public String getJavadocMockFor(Element element) {
        if (element instanceof ReflectionTypeMirror)
            return getJavadocMockFor(((ReflectionTypeMirror) element).asAnnotatedElement());
        return getJavadocMockFor(((ReflectionTypeMirror) element.asType()).asAnnotatedElement());
    }

    private String getJavadocMockFor(AnnotatedElement annotatedElement) {
        return javadocMocks.get(annotatedElement.toString());
    }
}
