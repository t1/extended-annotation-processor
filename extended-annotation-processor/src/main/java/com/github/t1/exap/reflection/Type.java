package com.github.t1.exap.reflection;

import static java.util.Objects.*;
import static javax.lang.model.element.ElementKind.*;
import static javax.lang.model.element.Modifier.*;
import static javax.lang.model.type.TypeKind.*;

import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

public class Type extends Elemental {
    public static Type of(TypeMirror type, ProcessingEnvironment env) {
        return new Type(env, type);
    }

    private final TypeMirror type;

    protected Type(ProcessingEnvironment processingEnv, TypeMirror type) {
        super(processingEnv);
        this.type = requireNonNull(type, "type");
    }

    @Override
    protected TypeElement getElement() {
        return asElement(type);
    }

    private TypeElement asElement(TypeMirror typeMirror) {
        return (TypeElement) types().asElement(typeMirror);
    }

    private boolean isKind(TypeKind kind) {
        return type.getKind() == kind;
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
        return types().isSameType(this.type, that.type);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getFullName();
    }

    public String getSimpleName() {
        if (getElement() == null)
            return type.toString();
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
            return ((TypeVariable) type).getUpperBound().toString();
        }
        return type.toString();
    }

    private boolean isType(Class<?> type) {
        return isKind(DECLARED) && getFullName().equals(type.getName());
    }

    public boolean isVoid() {
        return isKind(VOID) || isType(Void.class);
    }

    public boolean isPrimitive() {
        return type.getKind().isPrimitive();
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
        return (getElement() == null) ? false : getElement().getKind() == ENUM;
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
            return Type.of(((ArrayType) type).getComponentType(), env());
        return null;
    }

    public List<Type> getTypeParameters() {
        List<Type> result = new ArrayList<>();
        if (isKind(DECLARED))
            for (TypeMirror arg : ((DeclaredType) type).getTypeArguments())
                result.add(Type.of(arg, env()));
        return result;
    }

    public boolean isA(Type type) {
        return isA(type.type);
    }

    public boolean isA(Class<?> type) {
        return isA(elements().getTypeElement(type.getName()).asType());
    }

    private boolean isA(TypeMirror thatType) {
        // The following methods return false for, e.g., a List<String> and java.util.Collection<E>
        // as they have different type arguments:
        // types().isAssignable(left, right);
        // types().isSameType(left, right)
        // types().isSubtype(right, left)
        // TODO we could also check the type parameters, i.e. if a List<String> is a List<Number>
        try {
            if (isSameRawType(this.type, thatType))
                return true;
            if (isVoid() || isPrimitive())
                return false;
            for (TypeMirror supertype : allTypes())
                if (isSameRawType(supertype, thatType))
                    return true;
            return false;
        } catch (Error e) {
            throw new Error(this.type + " isSubclassOf " + thatType, e);
        }
    }

    private boolean isSameRawType(TypeMirror leftMirror, TypeMirror rightMirror) {
        String left = toRawString(leftMirror);
        String right = toRawString(rightMirror);
        return left.equals(right);
    }

    private String toRawString(TypeMirror mirror) {
        String string = mirror.toString();
        if (string.contains("<"))
            string = string.substring(0, string.indexOf('<'));
        return string;
    }

    private List<TypeMirror> allTypes() {
        Set<TypeMirror> result = new LinkedHashSet<>();
        for (TypeMirror t = type; t.getKind() != TypeKind.NONE; t = superClass(t)) {
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

    public List<Method> getAllMethods() {
        List<Method> methods = new ArrayList<>();
        methods.addAll(getMethods());
        if (getSuperType() != null)
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
                        list.add(new Method(env(), this, (ExecutableElement) element));
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
        List<Field> fields = new ArrayList<>();
        fields.addAll(getFields());
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
                        fields.add(new Field(env(), this, (VariableElement) element));
        return fields;
    }

    public Field getField(String name) {
        for (Field field : getFields())
            if (field.getName().equals(name))
                return field;
        throw new RuntimeException("field not found: " + name + ".\n  Only knows: " + getFields());
    }

    public Type getSuperType() {
        if (getElement() == null || getElement().getSuperclass().getKind() == NONE)
            return null;
        return Type.of(getElement().getSuperclass(), env());
    }

    public Package getPackage() {
        return new Package(env(), elements().getPackageOf(((DeclaredType) type).asElement()));
    }
}
