package com.github.t1.exap.reflection;

import com.github.t1.exap.insight.Elemental;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
import static java.util.stream.Collectors.toList;

class ReflectionElementUtils implements Elements {

    @Override
    public PackageElement getPackageElement(CharSequence name) {return null;}

    @Override
    public TypeElement getTypeElement(CharSequence name) {
        try {
            var type = ENV.type(Class.forName(name.toString()));
            return new ReflectionTypeElement(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(name + " not found", e);
        }
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a) {
        return ((ReflectionAnnotationMirror) a).getElementValuesWithDefaults();
    }

    @Override public String getDocComment(Element e) {return ENV.getJavadocMockFor(e);}

    @Override public boolean isDeprecated(Element e) {return false;}

    @Override public Name getBinaryName(TypeElement type) {return null;}

    @Override public PackageElement getPackageOf(Element type) {return null;}

    @Override public List<? extends Element> getAllMembers(TypeElement type) {return null;}

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
        if (e instanceof ReflectionTypeElement t)
            return Stream.concat(
                            e.getAnnotationMirrors().stream(),
                            t.type.superTypes().flatMap(Elemental::annotationMirrors))
                    .collect(toList());
        else return e.getAnnotationMirrors();
    }

    @Override public boolean hides(Element hider, Element hidden) {return false;}

    @Override
    public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
        return false;
    }

    @Override public String getConstantExpression(Object value) {return null;}

    @Override public void printElements(Writer w, Element... elements) {}

    @Override public Name getName(CharSequence cs) {return null;}

    @Override public boolean isFunctionalInterface(TypeElement type) {return false;}
}
