package com.github.t1.exap.reflection;

import static java.util.Objects.*;
import static javax.lang.model.type.TypeKind.*;

import java.util.*;

import javax.lang.model.element.*;
import javax.lang.model.type.*;

import com.github.t1.exap.Round;

public class Field extends Elemental {
    public static Field of(Element element, Round round) {
        return Type.of(element.getEnclosingElement().asType(), round).getField(element.getSimpleName().toString());
    }

    private final Type declaringType;
    private final VariableElement field;

    Field(Type declaringType, VariableElement field, Round round) {
        super(round);
        this.declaringType = requireNonNull(declaringType);
        this.field = requireNonNull(field);
    }

    @Override
    protected VariableElement getElement() {
        return field;
    }

    public String getName() {
        return field.getSimpleName().toString();
    }

    public Type getDeclaringType() {
        return declaringType;
    }

    public Type getType() {
        try {
            return Type.of(field.asType(), round());
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
                list.add(Type.of(typeMirror, round()));
        return list;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getType().getSimpleName() + "#" + getName();
    }
}
