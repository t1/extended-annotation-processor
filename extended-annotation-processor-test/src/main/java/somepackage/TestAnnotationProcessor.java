package somepackage;

import static javax.lang.model.SourceVersion.*;
import static javax.tools.StandardLocation.*;

import java.io.*;
import java.util.*;

import javax.annotation.processing.SupportedSourceVersion;
import javax.json.Json;
import javax.json.stream.*;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.tools.FileObject;

import org.slf4j.*;

import com.github.t1.exap.*;
import com.github.t1.exap.reflection.*;

@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationClasses({ MarkerAnnotation.class })
public class TestAnnotationProcessor extends ExtendedAbstractProcessor {
    private static final Logger log = LoggerFactory.getLogger(TestAnnotationProcessor.class);

    @Override
    public boolean process(Round round) throws IOException {
        log.info("start annotation processor");
        FileObject fileObject = filer().createResource(CLASS_OUTPUT, "", "round-" + round.number());
        try (JsonGenerator json = createJsonGenerator(fileObject.openWriter())) {
            json.writeStartArray();
            for (Type type : round.typesAnnotatedWith(MarkerAnnotation.class)) {
                type.warning("marked warning in round-" + round.number());
                json.writeStartObject();
                writeType(json, type);
                writeFields(json, type);
                writeMethods(json, type);
                json.writeEnd();
            }
            json.writeEnd();
        }
        return false;
    }

    private JsonGenerator createJsonGenerator(Writer out) {
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonGeneratorFactory factory = Json.createGeneratorFactory(properties);
        return factory.createGenerator(out);
    }

    private void writeType(JsonGenerator json, Type type) {
        log.debug("write type {}", type.getFullName());
        json.write("name", type.getSimpleName());
        json.write("fullName", type.getFullName());
        writeTypeParameters(json, type);
        json.write("isCollection", type.isA(Collection.class));
        json.write("void", type.isVoid());
        json.write("boolean", type.isBoolean());
        json.write("number", type.isNumber());
        json.write("integer", type.isInteger());
        json.write("decimal", type.isDecimal());
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

        writeAnnotations(json, type);
    }

    private void writeAnnotations(JsonGenerator json, Elemental type) {
        log.debug("write annotations on {}", type);
        json.writeStartArray("annotations");

        for (AnnotationWrapper annotation : type.getAnnotationWrappers()) {
            log.debug("write annotation {} on {}", annotation, type);
            json.writeStartObject();
            json.write("name", annotation.getAnnotationType().getSimpleName());
            json.write("fullName", annotation.getAnnotationType().getFullName());
            for (String name : annotation.getValueNames()) {
                json.writeStartArray(name);
                Object value = annotation.getValue(name);
                writeAnnotationPropertyValue(json, value);
                json.writeEnd();
            }
            json.writeEnd();
        }

        json.writeEnd();
    }

    private void writeAnnotationPropertyValue(JsonGenerator json, Object value) {
        log.debug("write {}:{}", (value == null) ? null : value.getClass(), value);
        if (value == null)
            json.writeNull();
        else if (value instanceof List)
            for (Object item : ((List<?>) value))
                writeAnnotationPropertyValue(json, item);
        else if (value instanceof Boolean)
            json.write((boolean) value);
        else if (value instanceof Character)
            json.write((char) value);
        else if (value instanceof Byte)
            json.write((byte) value);
        else if (value instanceof Short)
            json.write((short) value);
        else if (value instanceof Integer)
            json.write((int) value);
        else if (value instanceof Long)
            json.write((long) value);
        else if (value instanceof Float)
            json.write((float) value);
        else if (value instanceof Double)
            json.write((double) value);
        else if (value instanceof String)
            json.write(value.toString());
        // FIXME remove dependency on javax.lang.model
        else if (value instanceof AnnotationValue)
            json.write(((AnnotationValue) value).getValue().toString());
        else if (value instanceof DeclaredType)
            json.write(((DeclaredType) value).toString());
        else if (value instanceof VariableElement)
            json.write(((VariableElement) value).getSimpleName().toString());
        else
            throw new IllegalArgumentException("unmapped annotations type " + typeInfo(value));
    }

    private String typeInfo(Object value) {
        StringBuilder out = new StringBuilder();
        for (Class<?> t = value.getClass(); t != null; t = t.getSuperclass())
            typeInfo(out, t);
        return out.toString();
    }

    private void typeInfo(StringBuilder out, Class<?> t) {
        out.append("[").append(t.getName()).append("]");
        for (Class<?> i : t.getInterfaces())
            typeInfo(out, i);
    }

    private void writeFields(JsonGenerator json, Type type) {
        json.writeStartObject("fields");
        for (Field field : type.getFields()) {
            log.debug("write field {}", field.getName());
            json.writeStartObject(field.getName());

            json.writeStartObject("type");
            json.write("name", field.getType().getSimpleName());
            json.write("fullName", field.getType().getFullName());
            writeTypeParameters(json, field.getType());
            json.write("isCollection", field.getType().isA(Collection.class));
            json.writeEnd();

            json.write("public", field.isPublic());
            json.write("static", field.isStatic());
            json.write("transient", field.isTransient());
            writeAnnotations(json, field);
            json.writeEnd();
        }
        json.writeEnd();
    }

    private void writeMethods(JsonGenerator json, Type type) {
        json.writeStartArray("methods");
        for (Method method : type.getMethods()) {
            log.debug("write method {}", method.getName());
            json.writeStartObject();

            json.write("name", method.getName());
            json.write("containerType", method.getContainerType().toString());

            writeReturnType(json, method.getReturnType());
            writeAnnotations(json, method);
            writeParameters(json, method);

            json.writeEnd();
        }
        json.writeEnd();
    }

    private void writeReturnType(JsonGenerator json, Type returnType) {
        json.writeStartObject("returnType");
        json.write("name", returnType.getSimpleName());
        json.write("fullName", returnType.getFullName());
        writeTypeParameters(json, returnType);
        json.write("isCollection", returnType.isA(Collection.class));
        json.writeEnd();
    }

    private void writeTypeParameters(JsonGenerator json, Type type) {
        json.writeStartArray("typeParameters");
        for (Type typeParameter : type.getTypeParameters())
            json.write(typeParameter.getFullName());
        json.writeEnd();
    }

    private void writeParameters(JsonGenerator json, Method method) {
        json.writeStartArray("parameters");
        for (Parameter parameter : method.getParameters()) {
            json.writeStartObject();
            json.write("name", parameter.getName());
            json.write("method", parameter.getMethod().getName());
            json.write("type", parameter.getType().getFullName());
            writeTypeParameters(json, parameter.getType());
            json.write("isCollection", parameter.getType().isA(Collection.class));
            json.writeEnd();
        }
        json.writeEnd();
    }
}
