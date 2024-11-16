package com.github.t1.exap.reflection;

import com.github.t1.exap.insight.AnnotationWrapper;
import com.github.t1.exap.insight.Method;
import com.github.t1.exap.insight.Parameter;
import com.github.t1.exap.insight.Type;

import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
import static java.util.Arrays.asList;

class ReflectionConstructor extends Method {
    private final java.lang.reflect.Constructor<?> constructor;
    private List<Parameter> parameters;

    public ReflectionConstructor(Type declaringType, java.lang.reflect.Constructor<?> constructor) {
        super(declaringType, new ReflectionExecutableElement(constructor), ENV.round());
        this.constructor = constructor;
    }

    @Override
    public String name() {return constructor.getName();}

    @Override
    public List<AnnotationWrapper> getAnnotationWrappers() {
        return ReflectionAnnotationWrapper.allOn(constructor);
    }

    @Override
    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return asList(constructor.getAnnotationsByType(type));
    }

    @Override
    public <T extends Annotation> List<AnnotationWrapper> getAnnotationWrappers(Class<T> type) {
        return ReflectionAnnotationWrapper.ofTypeOn(constructor, type);
    }

    @Override
    public List<Parameter> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<>();
            for (int i = 0; i < constructor.getParameterTypes().length; i++)
                parameters.add(new ReflectionParameter(this, constructor.getParameters()[i]));
        }
        return parameters;
    }

    @Override
    public Parameter getParameter(int index) {return getParameters().get(index);}

    @Override
    public Type getReturnType() {return ReflectionType.type(constructor.getDeclaringClass());}

    @Override
    public boolean is(Modifier modifier) {return ReflectionModifiers.on(constructor.getModifiers()).is(modifier);}

    @Override
    protected void message(Diagnostic.Kind kind, CharSequence message) {ENV.message(this, kind, message);}
}
