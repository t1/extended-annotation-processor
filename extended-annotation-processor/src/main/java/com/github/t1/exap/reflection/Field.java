package com.github.t1.exap.reflection;

import static java.util.Objects.*;
import static javax.lang.model.type.TypeKind.*;

import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;

public class Field extends Elemental {
    private final VariableElement field;

    public Field(ProcessingEnvironment processingEnv, VariableElement field) {
        super(processingEnv);
        this.field = requireNonNull(field);
    }

    @Override
    protected VariableElement getElement() {
        return field;
    }

    public String getName() {
        return field.getSimpleName().toString();
    }

    public Type getType() {
        try {
            return Type.of(field.asType(), env());
        } catch (RuntimeException e) {
            throw new RuntimeException("while getting type of field " + field.getSimpleName(), e);
        } catch (Error e) {
            throw new Error("while getting type of field " + field.getSimpleName(), e);
        }
    }

    public List<Type> getTypeParameters() {
        List<Type> list = new ArrayList<>();
        if (field.asType().getKind() == DECLARED)
            for (TypeMirror typeMirror : ((DeclaredType) field.asType()).getTypeArguments())
                list.add(Type.of(typeMirror, env()));
        return list;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getType().getSimpleName() + "#" + getName();
    }
}
