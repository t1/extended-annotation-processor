package com.github.t1.exap.reflection;

import static javax.lang.model.element.ElementKind.*;
import static javax.lang.model.element.Modifier.*;

import java.lang.annotation.Annotation;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;

public class Type extends Messaged {
    private final TypeElement type;

    public Type(Messager messager, TypeElement type) {
        super(messager, type);
        this.type = type;
    }

    public boolean isPublic() {
        return type.getModifiers().contains(PUBLIC);
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return this.type.getAnnotation(type);
    }

    public void accept(TypeScanner scanner) {
        for (Element element : type.getEnclosedElements())
            if (element.getKind() == METHOD)
                scanner.visit(new Method(messager, (ExecutableElement) element));
    }

    @Override
    public String toString() {
        return "Type:" + type.getQualifiedName();
    }
}
