package com.github.t1.exap.reflection;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.ElementKind.METHOD;

class ReflectionExecutableElement implements ReflectionTypeMirror, ExecutableElement {
    private final Method method;

    ReflectionExecutableElement(Method method) {this.method = method;}

    @Override public AnnotatedElement asAnnotatedElement() {return method;}

    @Override public TypeMirror asType() {return null;}

    @Override public ElementKind getKind() {return METHOD;}

    @Override public Set<Modifier> getModifiers() {return ReflectionModifiers.on(method.getModifiers()).toSet();}

    @Override public List<? extends TypeParameterElement> getTypeParameters() {return List.of();}

    @Override public TypeMirror getReturnType() {return null;}

    @Override public List<? extends VariableElement> getParameters() {return List.of();}

    @Override public TypeMirror getReceiverType() {return null;}

    @Override public boolean isVarArgs() {return false;}

    @Override public boolean isDefault() {return false;}

    @Override public List<? extends TypeMirror> getThrownTypes() {return List.of();}

    @Override public AnnotationValue getDefaultValue() {return null;}

    @Override public Name getSimpleName() {return new ReflectionName(method.getName());}

    @Override public Element getEnclosingElement() {return null;}

    @Override public List<? extends Element> getEnclosedElements() {return List.of();}

    @Override public List<? extends AnnotationMirror> getAnnotationMirrors() {return List.of();}

    @Override public <A extends Annotation> A getAnnotation(Class<A> annotationType) {return null;}

    @Override public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {return null;}

    @Override public <R, P> R accept(ElementVisitor<R, P> v, P p) {return null;}
}
