package somepackage;

import static javax.lang.model.SourceVersion.*;
import static javax.tools.StandardLocation.*;

import java.io.*;
import java.util.*;

import javax.annotation.processing.SupportedSourceVersion;
import javax.json.Json;
import javax.json.stream.*;
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
                log.info("process {}", type);
                type.warning("marked warning in round-" + round.number());
                writeType(json, type);
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
        json.writeStartObject();

        json.write("type", type.getQualifiedName());
        writeJavadoc(json, type);
        writeFields(json, type);
        writeMethods(json, type);

        json.writeEnd();
    }

    private void writeJavadoc(JsonGenerator json, Type type) {
        json.writeStartObject("javadoc");
        json.write("summary", type.getAnnotation(JavaDoc.class).summary());
        json.write("value", type.getAnnotation(JavaDoc.class).value());
        json.writeEnd();
    }

    private void writeFields(JsonGenerator json, Type type) {
        json.writeStartArray("fields");
        for (Field field : type.getFields())
            json.write(field.getName());
        json.writeEnd();
    }

    private void writeMethods(JsonGenerator json, Type type) {
        json.writeStartArray("methods");
        for (Method method : type.getMethods())
            json.write(method.getName());
        json.writeEnd();
    }
}
