package com.github.t1.exap.insight;

import com.github.t1.exap.Round;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.BOOLEAN;
import static javax.lang.model.type.TypeKind.BYTE;
import static javax.lang.model.type.TypeKind.CHAR;
import static javax.lang.model.type.TypeKind.DECLARED;
import static javax.lang.model.type.TypeKind.DOUBLE;
import static javax.lang.model.type.TypeKind.FLOAT;
import static javax.lang.model.type.TypeKind.INT;
import static javax.lang.model.type.TypeKind.LONG;
import static javax.lang.model.type.TypeKind.NONE;
import static javax.lang.model.type.TypeKind.SHORT;
import static javax.lang.model.type.TypeKind.TYPEVAR;
import static javax.lang.model.type.TypeKind.VOID;

public class Type extends Elemental {
    public static Type of(TypeMirror type, Round round) {
        return new Type(type, round);
    }

    private final TypeMirror typeMirror;

    protected Type(TypeMirror typeMirror, Round round) {
        super(round);
        this.typeMirror = requireNonNull(typeMirror, "type");
    }

    public TypeMirror getTypeMirror() {return typeMirror;}

    @Override
    protected TypeElement getElement() {
        return asElement(typeMirror);
    }

    private TypeElement asElement(TypeMirror typeMirror) {
        return (TypeElement) types().asElement(typeMirror);
    }

    public TypeKind getKind() {
        return typeMirror.getKind();
    }

    private boolean isKind(TypeKind kind) {
        return typeMirror.getKind() == kind;
    }

    public void accept(TypeVisitor scanner) {
        for (Method method : getMethods())
            scanner.visit(method);
        for (Field field : getFields())
            scanner.visit(field);
    }

    @Override
    public int hashCode() {
        return typeMirror.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Type that = (Type) obj;
        return types().isSameType(this.typeMirror, that.typeMirror);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getFullName();
    }

    public String getSimpleName() {
        if (getElement() == null)
            return typeMirror.toString();
        return getElement().getSimpleName().toString();
    }

    /**
     * The {@link #getFullName() full name}, but without the package. This is generally the same as the
     * {@link #getSimpleName() simple name}, but for nested types, it makes a difference. And a nested '$' is replaced
     * with a '.', so it can be used in source.
     */
    public String getRelativeName() {
        String result = getFullName();
        String pkg = getPackage().getName();
        if (!pkg.isEmpty())
            pkg += ".";
        result = result.substring(pkg.length());
        return result.replace('$', '.');
    }

    /**
     * The fully qualified name plus the fully qualified type parameters, e.g.
     * <code>java.util.List&lt;java.lang.String&gt;</code>.
     */
    public String getFullName() {
        if (isKind(TYPEVAR)) {
            return ((TypeVariable) typeMirror).getUpperBound().toString();
        }
        return typeMirror.toString();
    }

    private boolean isType(Class<?> type) {
        return isKind(DECLARED) && getFullName().equals(type.getName());
    }

    public boolean isVoid() {
        return isKind(VOID) || isType(Void.class);
    }

    public boolean isPrimitive() {
        return typeMirror.getKind().isPrimitive();
    }

    public boolean isBoolean() {
        return isKind(BOOLEAN) || isType(Boolean.class);
    }

    public boolean isCharacter() {
        return isKind(CHAR) || isType(Character.class);
    }

    public boolean isNumber() {
        return isInteger() || isFloating();
    }

    public boolean isInteger() {
        return isKind(BYTE) || isType(Byte.class)//
               || isKind(SHORT) || isType(Short.class)//
               || isKind(INT) || isType(Integer.class)//
               || isKind(LONG) || isType(Long.class);
    }

    public boolean isFloating() {
        return isKind(FLOAT) || isType(Float.class)//
               || isKind(DOUBLE) || isType(Double.class);
    }

    public boolean isString() {
        return isType(String.class);
    }

    public boolean isEnum() {
        return getElement() != null && getElement().getKind() == ENUM;
    }

    public List<String> getEnumValues() {
        if (!isEnum())
            return null;
        List<String> values = new ArrayList<>();
        if (getElement() != null)
            for (Element element : getElement().getEnclosedElements())
                if (element.getKind() == ENUM_CONSTANT)
                    values.add(element.getSimpleName().toString());
        return values;
    }

    public boolean isArray() {
        return isKind(ARRAY);
    }

    public Type elementType() {
        if (isArray())
            return Type.of(((ArrayType) typeMirror).getComponentType(), round());
        return null;
    }

    public List<Type> getTypeParameters() {
        List<Type> result = new ArrayList<>();
        if (isKind(DECLARED))
            for (TypeMirror arg : ((DeclaredType) typeMirror).getTypeArguments())
                result.add(Type.of(arg, round()));
        return result;
    }

    public boolean isA(Type type) {
        // The following methods return false for, e.g., a List<String> and java.util.Collection<E>
        // as they have different type arguments:
        // types().isAssignable(left, right);
        // types().isSameType(left, right)
        // types().isSubtype(right, left)
        // TODO we could also check the type parameters, i.e. if a List<String> is a List<Number>
        return isA(toRawString(type.typeMirror));
    }

    public boolean isA(Class<?> type) {
        return isA(type.getName());
    }

    private boolean isA(String thatTypeName) {
        try {
            if (toRawString(this.typeMirror).equals(thatTypeName))
                return true;
            if (isVoid() || isPrimitive())
                return false;
            for (TypeMirror supertype : allTypes())
                if (toRawString(supertype).equals(thatTypeName))
                    return true;
            return false;
        } catch (Error e) {
            throw new Error(this.typeMirror + " isSubclassOf " + thatTypeName, e);
        }
    }

    private String toRawString(TypeMirror mirror) {
        String string = mirror.toString();
        if (string.contains("<"))
            string = string.substring(0, string.indexOf('<'));
        return string;
    }

    private List<TypeMirror> allTypes() {
        Set<TypeMirror> result = new LinkedHashSet<>();
        for (TypeMirror t = typeMirror; t.getKind() != TypeKind.NONE; t = superClass(t)) {
            result.add(t);
            addInterfaces(result, t);
        }
        return new ArrayList<>(result);
    }

    private TypeMirror superClass(TypeMirror typeMirror) {
        TypeElement element = asElement(typeMirror);
        return element.getSuperclass();
    }

    private void addInterfaces(Set<TypeMirror> result, TypeMirror t) {
        for (TypeMirror i : asElement(t).getInterfaces()) {
            result.add(i);
            addInterfaces(result, i);
        }
    }

    public Stream<Type> interfaces() {
        return getElement().getInterfaces().stream()
                .map(i -> new Type(i, round()));
    }

    public Stream<Type> allInterfaces() {
        return Stream.concat(this.interfaces(), superTypes().flatMap(Type::interfaces));
    }

    public List<Method> getAllMethods() {
        List<Method> methods = new ArrayList<>(getMethods());
        if (hasSuperType())
            methods.addAll(getSuperType().getAllMethods());
        return methods;
    }

    public List<Method> getMethods() {
        return getMethods(false);
    }

    public List<Method> getStaticMethods() {
        return getMethods(true);
    }

    private List<Method> getMethods(boolean isStatic) {
        List<Method> list = new ArrayList<>();
        if (getElement() != null)
            for (Element element : getElement().getEnclosedElements())
                if (element.getModifiers().contains(STATIC) == isStatic)
                    if (element.getKind() == METHOD)
                        list.add(new Method(this, (ExecutableElement) element, round()));
        return list;
    }

    public Method getMethod(String name) {
        for (Method method : getMethods())
            if (method.getName().equals(name))
                return method;
        throw new RuntimeException("method not found: " + name + ".\n  Only knows: " + getMethods());
    }


    public boolean hasMethod(String name) {
        for (Method method : getMethods())
            if (method.getName().equals(name))
                return true;
        return false;
    }

    public List<Field> getAllFields() {
        List<Field> fields = new ArrayList<>(getFields());
        if (getSuperType() != null)
            fields.addAll(getSuperType().getAllFields());
        return fields;
    }

    public List<Field> getFields() {
        return getFields(false);
    }

    public List<Field> getStaticFields() {
        return getFields(true);
    }

    private List<Field> getFields(boolean isStatic) {
        List<Field> fields = new ArrayList<>();
        if (getElement() != null)
            for (Element element : getElement().getEnclosedElements())
                if (element.getModifiers().contains(STATIC) == isStatic)
                    if (element.getKind() == FIELD)
                        fields.add(new Field(this, (VariableElement) element, round()));
        return fields;
    }

    public Field getField(String name) {
        for (Field field : getFields())
            if (field.getName().equals(name))
                return field;
        throw new RuntimeException("field not found: " + name + ".\n  Only knows: " + getFields());
    }

    public Optional<Type> superType() {
        return hasSuperType() ? Optional.of(Type.of(getElement().getSuperclass(), round())) : Optional.empty();
    }

    public Type getSuperType() {
        return superType().orElseThrow();
    }

    public boolean hasSuperType() {
        return getElement() != null && getElement().getSuperclass().getKind() != NONE;
    }

    public Stream<Type> superTypes() {
        return hasSuperType() ?
                Stream.iterate(getSuperType(), Type::hasSuperType, Type::getSuperType) :
                Stream.empty();
    }

    public Package getPackage() {
        return new Package(elements().getPackageOf(((DeclaredType) typeMirror).asElement()), round());
    }
}
