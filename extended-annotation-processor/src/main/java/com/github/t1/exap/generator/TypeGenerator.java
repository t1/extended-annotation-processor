package com.github.t1.exap.generator;

import com.github.t1.exap.reflection.Package;
import com.github.t1.exap.reflection.Resource;
import com.github.t1.exap.reflection.Type;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

import static com.github.t1.exap.generator.TypeKind.CLASS;
import static java.lang.String.join;
import static java.util.Collections.emptyList;

public class TypeGenerator implements AutoCloseable {
    private final Logger log;
    private final Package pkg;
    private final String typeName;

    private final ImportGenerator imports;
    private JavaDocGenerator javaDoc;
    private TypeKind kind = CLASS;
    private List<String> typeParameters;
    private List<String> implementsList;
    private List<AnnotationGenerator> annotations;
    private List<FieldGenerator> fields;
    private List<ConstructorGenerator> constructors;
    private List<MethodGenerator> methods;

    public TypeGenerator(Logger log, Package pkg, String typeName) {
        this.log = log;
        this.pkg = pkg;
        this.typeName = typeName;
        this.imports = new ImportGenerator(pkg);
    }

    public void javaDoc(String javaDoc) {
        if (javaDoc != null && !javaDoc.isEmpty())
            this.javaDoc = new JavaDocGenerator("", javaDoc);
    }

    public void kind(TypeKind kind) {
        this.kind = kind;
    }

    public TypeKind kind() {
        return kind;
    }

    /** You should only need to call this for types needed <em>in</em> your body */
    public TypeGenerator addImport(Type type) {
        imports.add(type);
        return this;
    }

    public TypeGenerator addImplements(TypeExpressionGenerator type) {
        return addImplements(type.toString());
    }

    public TypeGenerator addImplements(String type) {
        if (implementsList == null) implementsList = new ArrayList<>();
        implementsList.add(type);
        return this;
    }

    public void addTypeParameter(String typeParameter) {
        if (typeParameters == null)
            typeParameters = new ArrayList<>();
        typeParameters.add(typeParameter);
    }

    public AnnotationGenerator annotation(Type type) {
        if (annotations == null)
            annotations = new ArrayList<>();
        AnnotationGenerator annotationGenerator = new AnnotationGenerator(this, type);
        annotations.add(annotationGenerator);
        return annotationGenerator;
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

    public List<String> getTypeParameters() {
        return (typeParameters == null) ? emptyList() : typeParameters;
    }


    @Override
    public void close() {
        Resource resource = pkg.createSource(typeName);
        log.debug("write {} to {}", typeName, resource.getName());
        try (Writer writer = resource.openWriter()) {
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
        if (!pkg.isRoot()) {
            out.println("package " + pkg.getName() + ";");
            out.println();
        }
        imports.print(out);
    }

    private void printType(PrintWriter out) {
        if (javaDoc != null)
            javaDoc.print(out);
        printAnnotations(out);
        out.append("public ").append(kind.toString()).append(" ").append(typeName);
        printTypeParams(out);
        if (implementsList != null && !implementsList.isEmpty())
            out.append(" implements ").append(join(", ", implementsList));
        out.println(" {");
        printMethods(out, MethodGenerator::isStatic);
        printFields(out);
        printConstructors(out);
        printMethods(out, m -> !m.isStatic());
        out.println("}");
    }

    private void printAnnotations(PrintWriter out) {
        if (annotations == null)
            return;
        for (AnnotationGenerator annotation : annotations)
            out.println(annotation.toString());
    }

    private void printTypeParams(PrintWriter out) {
        if (typeParameters == null)
            return;
        StringJoiner joiner = new StringJoiner(", ", "<", ">");
        for (String typeParameter : typeParameters)
            joiner.add(typeParameter);
        out.print(joiner);
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
