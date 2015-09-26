package com.github.t1.exap.reflection;

import static javax.lang.model.element.ElementKind.*;
import static javax.lang.model.type.TypeKind.*;

import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

import org.slf4j.*;

public class Type extends Elemental {
    private static final Logger log = LoggerFactory.getLogger(Type.class);

    public static Type of(TypeMirror type, ProcessingEnvironment env) {
        TypeKind kind = type.getKind();
        switch (kind) {
            case BOOLEAN:
                return of(boolean.class);
            case BYTE:
                return of(byte.class);
            case CHAR:
                return of(char.class);
            case DOUBLE:
                return of(double.class);
            case FLOAT:
                return of(float.class);
            case INT:
                return of(int.class);
            case LONG:
                return of(long.class);
            case SHORT:
                return of(short.class);
            case VOID:
                return of(void.class);

            case ARRAY:
            case DECLARED:
                TypeElement typeElement = (TypeElement) env.getTypeUtils().asElement(type);
                return new Type(env, typeElement);
            case ERROR:
                throw new RuntimeException("error type kind: " + kind + ": " + type);
            case EXECUTABLE:
            case INTERSECTION:
            case NONE:
            case NULL:
            case OTHER:
            case PACKAGE:
            case TYPEVAR:
            case UNION:
            case WILDCARD:
                throw new RuntimeException("unexpected type kind: " + kind + ": " + type);
        }
        throw new UnsupportedOperationException("unsupported type kind: " + kind + ": " + type);
    }

    public static Type of(java.lang.reflect.Type type) {
        return ReflectionType.type(type);
    }

    private final TypeElement type;

    public Type(ProcessingEnvironment processingEnv, TypeElement type) {
        super(processingEnv, type);
        this.type = Objects.requireNonNull(type, "type");
    }

    private TypeKind typeKind() {
        return type.asType().getKind();
    }

    private ElementKind kind() {
        return type.getKind();
    }

    public void accept(TypeVisitor scanner) {
        for (Method method : getMethods())
            scanner.visit(method);
        for (Field field : getFields())
            scanner.visit(field);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Type that = (Type) obj;
        return types().isSameType(this.type.asType(), that.type.asType());
    }

    @Override
    public String toString() {
        return "Type:" + getQualifiedName();
    }

    public String getQualifiedName() {
        Name name = type.getQualifiedName();
        if (name == null)
            name = type.getSimpleName();
        return name.toString();
    }

    public String getSimpleName() {
        return type.getSimpleName().toString();
    }

    public boolean isVoid() {
        return typeKind() == VOID;
    }

    public boolean isBoolean() {
        return typeKind() == BOOLEAN;
    }

    public boolean isNumber() {
        return isInteger() || isDecimal();
    }

    public boolean isInteger() {
        return typeKind() == BYTE || typeKind() == SHORT || typeKind() == INT || typeKind() == LONG;
    }

    public boolean isDecimal() {
        return typeKind() == FLOAT || typeKind() == DOUBLE;
    }

    public boolean isString() {
        return type.getQualifiedName().contentEquals(String.class.getName());
    }

    public boolean isEnum() {
        return kind() == ENUM;
    }

    public List<String> getEnumValues() {
        if (!isEnum())
            return null;
        List<String> values = new ArrayList<>();
        for (Element element : type.getEnclosedElements())
            if (element.getKind() == ENUM_CONSTANT)
                values.add(element.getSimpleName().toString());
        return values;
    }

    public boolean isArray() {
        return typeKind() == ARRAY;
    }

    public Type elementType() {
        if (isArray())
            return Type.of(((ArrayType) type.asType()).getComponentType(), env());
        return null;
    }

    public List<TypeParameter> getTypeParameters() {
        log.debug("type parameters of {}", type);
        List<TypeParameter> result = new ArrayList<>();
        for (TypeParameterElement parameterElement : type.getTypeParameters()) {
            log.debug("    type parameter: {} generic: {}", parameterElement.getSimpleName(),
                    parameterElement.getGenericElement());
            List<Type> bounds = new ArrayList<>();
            for (TypeMirror bound : parameterElement.getBounds())
                bounds.add(Type.of(bound, env()));
            log.debug("    bounds: {}", bounds);
            result.add(new TypeParameter(parameterElement.getSimpleName().toString(), bounds));
        }
        return result;
    }

    public boolean isAssignableTo(Class<?> type) {
        try {
            TypeMirror targetType = elements().getTypeElement(type.getName()).asType();
            if (isSameType(this.type.asType(), targetType))
                return true;
            for (TypeMirror supertype : types().directSupertypes(this.type.asType()))
                if (isSameType(targetType, supertype))
                    return true;
            return false;
        } catch (Error e) {
            throw new Error(this.type + " isSubclassOf " + type, e);
        }
    }

    private boolean isSameType(TypeMirror left, TypeMirror right) {
        // why do these return false for java.util.Collection<E>?
        // types().isAssignable(left, right);
        // types().isSameType(left, right)
        // left.equals(right),
        return left.toString().equals(right.toString());
    }

    public List<Method> getMethods() {
        List<Method> list = new ArrayList<>();
        for (Element element : type.getEnclosedElements())
            if (element.getKind() == METHOD)
                list.add(new Method(env(), this, (ExecutableElement) element));
        return list;
    }

    public Method getMethod(String name) {
        for (Method method : getMethods())
            if (method.getName().equals(name))
                return method;
        throw new RuntimeException("method not found: " + name + ".\n  Only knows: " + getMethods());
    }

    public List<Field> getFields() {
        List<Field> fields = new ArrayList<>();
        for (Element enclosedElement : type.getEnclosedElements())
            if (enclosedElement instanceof VariableElement)
                fields.add(new Field(env(), (VariableElement) enclosedElement));
        return fields;
    }

    public Field getField(String name) {
        for (Field field : getFields())
            if (field.getName().equals(name))
                return field;
        throw new RuntimeException("field not found: " + name + ".\n  Only knows: " + getFields());
    }

    public List<Method> getAllMethods() {
        List<Method> methods = new ArrayList<>();
        methods.addAll(getMethods());
        if (getSuperType() != null)
            methods.addAll(getSuperType().getAllMethods());
        return methods;
    }

    public Type getSuperType() {
        return Type.of(type.getSuperclass(), env());
    }
}
