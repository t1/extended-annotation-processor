package com.github.t1.exap.insight;

import com.github.t1.exap.Round;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static javax.lang.model.type.TypeKind.DECLARED;

public class Field extends Elemental {
    public static Field of(Element element, Round round) {
        return Type.of(element.getEnclosingElement().asType(), round).getField(element.getSimpleName().toString());
    }

    private final Type declaringType;
    private final VariableElement field;

    public Field(Type declaringType, VariableElement field, Round round) {
        super(round);
        this.declaringType = requireNonNull(declaringType);
        this.field = requireNonNull(field);
    }

    @Override public VariableElement getElement() {return field;}

    @Override public Optional<Elemental> enclosingElement() {return Optional.of(declaringType);}

    public String name() {return field.getSimpleName().toString();}

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

    @Override public String toString() {return declaringType + "#" + name();}
}
