package com.github.t1.exap.reflection;

import com.github.t1.exap.insight.AnnotationWrapper;
import com.github.t1.exap.insight.Field;
import com.github.t1.exap.insight.Type;

import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.List;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
import static java.util.Arrays.asList;

class ReflectionField extends Field {
    private final java.lang.reflect.Field field;

    public ReflectionField(Type declaringType, java.lang.reflect.Field field) {
        super(declaringType, new ReflectionVariableElement(field), ENV.round());
        this.field = field;
    }

    @Override public String name() {return field.getName();}

    @Override public Type getType() {return ReflectionType.type(field.getGenericType());}

    @Override
    protected boolean is(Modifier modifier) {
        return ReflectionModifiers.on(field.getModifiers()).is(modifier);
    }

    @Override
    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return asList(field.getAnnotationsByType(type));
    }

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return ReflectionAnnotationWrapper.allOn(field);
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return ReflectionAnnotationWrapper.ofTypeOn(field, type);
    }

    @Override
    protected void message(Diagnostic.Kind kind, CharSequence message) {
        ENV.message(this, kind, message);
    }
}
