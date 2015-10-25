package somepackage;

import java.io.Writer;
import java.util.*;

import javax.json.Json;
import javax.json.stream.*;

import org.slf4j.*;

import com.github.t1.exap.LoggingJsonGenerator;
import com.github.t1.exap.reflection.*;

class TestGenerator implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TestGenerator.class);

    private final JsonGenerator json;

    public TestGenerator(Writer writer) {
        this.json = LoggingJsonGenerator.of(log, createJsonGenerator(writer));
    }

    private JsonGenerator createJsonGenerator(Writer out) {
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonGeneratorFactory factory = Json.createGeneratorFactory(properties);
        return factory.createGenerator(out);
    }

    public void write(List<Type> types) {
        json.writeStartArray();
        for (Type type : types) {
            json.writeStartObject();
            writeType(type);
            writeFields(type);
            writeMethods(type);
            json.writeEnd();
        }
        json.writeEnd();
    }

    private void writeType(Type type) {
        log.debug("write type {}", type.getFullName());
        json.write("name", type.getSimpleName());
        json.write("fullName", type.getFullName());
        writeTypeParameters(type);
        json.write("collection", type.isA(Collection.class));
        json.write("void", type.isVoid());
        json.write("boolean", type.isBoolean());
        json.write("number", type.isNumber());
        json.write("integer", type.isInteger());
        json.write("decimal", type.isFloating());
        json.write("string", type.isString());
        json.write("enum", type.isEnum());
        json.writeStartArray("enumValues");
        if (type.getEnumValues() != null)
            for (String enumValue : type.getEnumValues())
                json.write(enumValue);
        json.writeEnd();
        json.write("array", type.isArray());
        if (type.elementType() == null)
            json.writeNull("elementType");
        else
            json.write("elementType", type.elementType().getFullName());
        json.write("public", type.isPublic());
        json.write("static", type.isStatic());
        json.write("transient", type.isTransient());

        writeAnnotations(type);
    }

    private void writeAnnotations(Elemental type) {
        log.debug("write annotations on {}", type);
        if (type.isAnnotated(MarkerAnnotation.class))
            type.warning("#" + type.getAnnotation(MarkerAnnotation.class).value());
        json.writeStartArray("annotations");

        for (AnnotationWrapper annotation : type.getAnnotationWrappers()) {
            String simpleName = annotation.getAnnotationType().getSimpleName();
            log.debug("write annotation {} on {}: {}", simpleName, type, annotation);
            json.writeStartObject();
            json.write("name", simpleName);
            json.write("fullName", annotation.getAnnotationType().getFullName());
            for (String name : annotation.getPropertyNames()) {
                json.writeStartArray(name);
                writeAnnotationPropertyValue(annotation, name);
                json.writeEnd();
            }
            json.writeEnd();
        }

        json.writeEnd();
    }

    private void writeAnnotationPropertyValue(AnnotationWrapper annotation, String name) {
        AnnotationPropertyType propertyType = annotation.getPropertyType(name);
        log.debug("write annotation property \"{}\" type {}{}", name, propertyType, //
                annotation.isArrayProperty(name) ? "[]" : "");
        switch (propertyType) {
            case ANNOTATION:
                for (Object value : annotation.getAnnotationProperties(name))
                    json.write(value.toString());
                return;
            case BOOLEAN:
                for (Boolean value : annotation.getBooleanProperties(name))
                    json.write(value);
                return;
            case BYTE:
                for (Byte value : annotation.getByteProperties(name))
                    json.write(value);
                return;
            case CHAR:
                for (Character value : annotation.getCharProperties(name))
                    json.write(value.toString());
                return;
            case CLASS:
                for (Type value : annotation.getTypeProperties(name))
                    json.write(value.getFullName());
                return;
            case DOUBLE:
                for (Double value : annotation.getDoubleProperties(name))
                    json.write(value);
                return;
            case ENUM:
                for (String value : annotation.getEnumProperties(name))
                    json.write(value);
                return;
            case FLOAT:
                for (Float value : annotation.getFloatProperties(name))
                    json.write(value);
                return;
            case INT:
                for (Integer value : annotation.getIntProperties(name))
                    json.write(value);
                return;
            case LONG:
                for (Long value : annotation.getLongProperties(name))
                    json.write(value);
                return;
            case SHORT:
                for (Short value : annotation.getShortProperties(name))
                    json.write(value);
                return;
            case STRING:
                for (String value : annotation.getStringProperties(name))
                    json.write(value);
                return;
        }
        throw new IllegalArgumentException("unmapped annotations type " + propertyType);
    }

    private void writeFields(Type type) {
        if (!type.getStaticFields().isEmpty()) {
            json.writeStartArray("staticFields");
            for (Field field : type.getStaticFields())
                json.write(field.getName());
            json.writeEnd();
        }

        json.writeStartObject("fields");
        for (Field field : type.getFields()) {
            log.debug("write field \"{}\"", field.getName());
            json.writeStartObject(field.getName());

            json.writeStartObject("type");
            json.write("name", field.getType().getSimpleName());
            json.write("fullName", field.getType().getFullName());
            writeTypeParameters(field.getType());
            json.write("collection", field.getType().isA(Collection.class));
            json.writeEnd();

            json.write("public", field.isPublic());
            json.write("static", field.isStatic());
            json.write("transient", field.isTransient());
            writeAnnotations(field);
            json.writeEnd();
        }
        json.writeEnd();
    }

    private void writeMethods(Type type) {
        if (!type.getStaticMethods().isEmpty()) {
            json.writeStartArray("staticMethods");
            for (Method method : type.getStaticMethods())
                json.write(method.getName());
            json.writeEnd();
        }

        json.writeStartArray("methods");
        for (Method method : type.getMethods()) {
            log.debug("write method \"{}\"", method.getName());
            json.writeStartObject();

            json.write("name", method.getName());
            json.write("containerType", method.getContainerType().toString());

            writeReturnType(method.getReturnType());
            writeAnnotations(method);
            writeParameters(method);

            json.writeEnd();
        }
        json.writeEnd();
    }

    private void writeReturnType(Type returnType) {
        json.writeStartObject("returnType");
        json.write("name", returnType.getSimpleName());
        json.write("fullName", returnType.getFullName());
        writeTypeParameters(returnType);
        json.write("is-a-collection", returnType.isA(Collection.class));
        json.write("is-a-container", returnType.isA(Container.class));
        json.writeEnd();
    }

    private void writeTypeParameters(Type type) {
        json.writeStartArray("typeParameters");
        for (Type typeParameter : type.getTypeParameters())
            json.write(typeParameter.getFullName());
        json.writeEnd();
    }

    private void writeParameters(Method method) {
        json.writeStartArray("parameters");
        for (Parameter parameter : method.getParameters()) {
            json.writeStartObject();
            json.write("name", parameter.getName());
            json.write("method", parameter.getMethod().getName());
            json.write("type", parameter.getType().getFullName());
            writeTypeParameters(parameter.getType());
            json.write("collection", parameter.getType().isA(Collection.class));
            writeAnnotations(parameter);
            json.writeEnd();
        }
        json.writeEnd();
    }

    @Override
    public void close() {
        json.close();
    }
}
