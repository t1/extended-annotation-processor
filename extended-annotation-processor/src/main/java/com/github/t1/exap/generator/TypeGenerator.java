package com.github.t1.exap.generator;

import com.github.t1.exap.Round;
import com.github.t1.exap.insight.Package;
import com.github.t1.exap.insight.Type;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.github.t1.exap.generator.TypeKind.CLASS;
import static com.github.t1.exap.generator.Visibility.PACKAGE_PRIVATE;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;

@SuppressWarnings("UnusedReturnValue")
public class TypeGenerator implements AutoCloseable {
    private final Round round;
    private final Package pkg;
    private final String typeName;

    private final ImportGenerator imports;
    private JavaDocGenerator javaDoc;
    private TypeKind kind = CLASS;
    private final List<String> typeParameters = new ArrayList<>();
    private final List<String> implementsList = new ArrayList<>();
    private final List<AnnotationGenerator> annotations = new ArrayList<>();
    private final List<ConstructorGenerator> constructors = new ArrayList<>();
    private final List<FieldGenerator> fields = new ArrayList<>();
    public final List<MethodGenerator> methods = new ArrayList<>();

    public TypeGenerator(Round round, Package pkg, String typeName) {
        this.round = round;
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
    public TypeGenerator addImport(String type) {
        return addImport(round.type(type));
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
        implementsList.add(type);
        return this;
    }

    public void addTypeParameter(String typeParameter) {
        typeParameters.add(typeParameter);
    }

    public AnnotationGenerator annotation(Type type) {
        var annotationGenerator = new AnnotationGenerator(this, type);
        annotations.add(annotationGenerator);
        return annotationGenerator;
    }

    public FieldGenerator addField(String name) {
        var fieldGenerator = new FieldGenerator(this, name);
        fields.add(fieldGenerator);
        return fieldGenerator;
    }

    public ConstructorGenerator addConstructor() {
        var constructorGenerator = new ConstructorGenerator(this);
        constructors.add(constructorGenerator);
        return constructorGenerator;
    }

    public MethodGenerator addMethod(String name) {return addMethod(PACKAGE_PRIVATE, name);}

    public MethodGenerator addMethod(Visibility visibility, String name) {
        var methodGenerator = new MethodGenerator(this, visibility, name);
        methods.add(methodGenerator);
        return methodGenerator;
    }


    public String getTypeName() {
        return typeName;
    }

    public List<String> getTypeParameters() {
        return typeParameters;
    }


    @Override
    public void close() {
        var resource = pkg.createSource(typeName);
        round.log().debug("write {} to {}", typeName, resource.getPath().getParent());
        try (var writer = resource.openWriter()) {
            print(new PrintWriter(writer));
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
        if (!implementsList.isEmpty())
            out.append(" implements ").append(join(", ", implementsList));
        out.println(" {");
        // In the body we print a newline before every constructor and method but not before fields.
        // So it's possible to start with a superfluous empty line if there are no fields
        // (which happens in most interfaces) or before static method, but not to end with one. Good enough.
        printMethods(out, MethodGenerator::isStatic);
        printFields(out);
        printConstructors(out);
        printMethods(out, m -> !m.isStatic());
        out.println("}");
    }

    private void printAnnotations(PrintWriter out) {
        annotations.stream().map(AnnotationGenerator::toString).forEach(out::println);
    }

    private void printTypeParams(PrintWriter out) {
        if (!typeParameters.isEmpty()) {
            out.print(typeParameters.stream().collect(joining(", ", "<", ">")));
        }
    }

    private void printConstructors(PrintWriter out) {
        constructors.forEach(constructor -> constructor.print(out));
    }

    private void printFields(PrintWriter out) {
        fields.forEach(field -> field.print(out));
    }

    private void printMethods(PrintWriter out, Predicate<MethodGenerator> filter) {
        methods.stream()
                .filter(filter)
                .forEach(method -> method.print(out));
    }
}
