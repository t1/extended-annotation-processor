package com.github.t1.exap.generator;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

import com.github.t1.exap.reflection.Package;
import com.github.t1.exap.reflection.Type;

public class TypeGenerator implements AutoCloseable {
    private final Package pkg;
    private final String typeName;
    private Set<Type> imports = new TreeSet<>(Comparator.comparing(c -> c.getFullName()));
    List<String> typeParameters;
    private List<FieldGenerator> fields;
    private List<MethodGenerator> methods;
    private List<ConstructorGenerator> constructors;

    public TypeGenerator(Package pkg, String typeName) {
        this.pkg = pkg;
        this.typeName = typeName;
    }

    TypeGenerator addImport(Type type) {
        if (requiresImport(type))
            imports.add(type);
        return this;
    }

    private boolean requiresImport(Type type) {
        if ("java.lang".equals(type.getPackage().getName()))
            return false;
        return true;
    }

    public void addTypeParameter(String typeParameter) {
        if (typeParameters == null)
            typeParameters = new ArrayList<>();
        typeParameters.add(typeParameter);
    }

    public FieldGenerator addField(String name) {
        if (fields == null)
            fields = new ArrayList<>();
        FieldGenerator fieldGenerator = new FieldGenerator(this, name);
        fields.add(fieldGenerator);
        return fieldGenerator;
    }

    public ConstructorGenerator addConstructor() {
        if (constructors == null)
            constructors = new ArrayList<>();
        ConstructorGenerator constructorGenerator = new ConstructorGenerator(this);
        constructors.add(constructorGenerator);
        return constructorGenerator;
    }

    public MethodGenerator addMethod(String name) {
        if (methods == null)
            methods = new ArrayList<>();
        MethodGenerator methodGenerator = new MethodGenerator(this, name);
        methods.add(methodGenerator);
        return methodGenerator;
    }


    public String getTypeName() {
        return typeName;
    }


    @Override
    public void close() {
        try (Writer writer = pkg.createResource(typeName).openWriter()) {
            PrintWriter out = new PrintWriter(writer);
            print(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void print(PrintWriter out) {
        printHeader(out);
        printType(out);
    }

    private void printHeader(PrintWriter out) {
        out.println("package " + pkg.getName() + ";");
        out.println();
        printImportGroup(out, "java");
        printImportGroup(out, "javax");
        printImportGroup(out, "org");
        printImportGroup(out, "com");
    }

    private void printImportGroup(PrintWriter out, String groupName) {
        boolean any = false;
        for (Type type : imports)
            if (type.getFullName().startsWith(groupName + ".")) {
                any = true;
                out.println("import " + type.getFullName().replace('$', '.') + ";");
            }
        if (any)
            out.println();
    }

    private void printType(PrintWriter out) {
        out.print("public class " + typeName);
        printTypeParams(out);
        out.println(" {");
        printMethods(out, m -> m.isStatic());
        printFields(out);
        printConstructors(out);
        printMethods(out, m -> !m.isStatic());
        out.println("}");
    }

    private void printTypeParams(PrintWriter out) {
        if (typeParameters == null)
            return;
        out.print("<");
        Delimiter delimiter = new Delimiter(", ");
        for (String typeParameter : typeParameters)
            out.append(delimiter.next()).print(typeParameter);
        out.print(">");
    }

    private void printMethods(PrintWriter out, Predicate<MethodGenerator> filter) {
        if (methods == null)
            return;
        for (MethodGenerator method : methods) {
            if (!filter.test(method))
                continue;
            method.print(out);
        }
    }

    private void printFields(PrintWriter out) {
        if (fields == null)
            return;
        for (FieldGenerator field : fields) {
            field.print(out);
        }
        out.println();
    }

    private void printConstructors(PrintWriter out) {
        if (constructors == null)
            return;
        for (ConstructorGenerator constructor : constructors) {
            constructor.print(out);
        }
    }
}
