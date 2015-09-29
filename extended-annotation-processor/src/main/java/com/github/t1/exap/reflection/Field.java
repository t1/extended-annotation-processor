package com.github.t1.exap.reflection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import org.slf4j.*;

public class Field extends Elemental {
    private static final Logger log = LoggerFactory.getLogger(Field.class);

    private final VariableElement field;

    public Field(ProcessingEnvironment processingEnv, VariableElement field) {
        super(processingEnv, field);
        this.field = field;
    }

    @Override
    protected VariableElement getElement() {
        return field;
    }

    public String getName() {
        return field.getSimpleName().toString();
    }

    public Type getType() {
        if (field.asType() instanceof DeclaredType)
            log.debug("########### {} : {}", ((DeclaredType) field.asType()).getTypeArguments(), field.asType());
        try {
            return Type.of(field.asType(), env());
        } catch (RuntimeException e) {
            throw new RuntimeException("while getting type of field " + field.getSimpleName(), e);
        } catch (Error e) {
            throw new Error("while getting type of field " + field.getSimpleName(), e);
        }
    }
}
