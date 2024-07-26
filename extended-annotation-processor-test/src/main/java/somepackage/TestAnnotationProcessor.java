package somepackage;

import com.github.t1.exap.ExtendedAbstractProcessor;
import com.github.t1.exap.Round;
import com.github.t1.exap.SupportedAnnotationClasses;
import com.github.t1.exap.generator.TypeGenerator;
import com.github.t1.exap.reflection.Field;
import com.github.t1.exap.reflection.Resource;
import com.github.t1.exap.reflection.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.SupportedSourceVersion;
import java.io.IOException;
import java.io.Writer;

import static com.github.t1.exap.generator.TypeKind.INTERFACE;
import static javax.lang.model.SourceVersion.RELEASE_8;

@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationClasses({MarkerAnnotation.class})
public class TestAnnotationProcessor extends ExtendedAbstractProcessor {
    private static final Logger log = LoggerFactory.getLogger(TestAnnotationProcessor.class);

    @Override
    public boolean process(Round round) throws IOException {
        log.info("start annotation processor");
        createRoundOutput(round);
        createFieldList(round);
        generateSourceFile(round);
        return false;
    }

    private void createRoundOutput(Round round) throws IOException {
        Resource resource = round.getRootPackage().createResource("round-" + round.number() + ".json");
        try (Writer writer = resource.openWriter()) {
            try (TestGenerator generator = new TestGenerator(writer)) {
                generator.write(round.typesAnnotatedWith(MarkerAnnotation.class));
            }
        }
    }

    private void createFieldList(Round round) throws IOException {
        Resource resource = round.getRootPackage().createResource("fields-" + round.number());
        try (Writer writer = resource.openWriter()) {
            for (Field field : round.fieldsAnnotatedWith(MarkerAnnotation.class)) {
                writer.append(field.getName()).append(":").append(field.getAnnotation(MarkerAnnotation.class).value()).append("\n");
            }
        }
    }

    private void generateSourceFile(Round round) {
        // depending on the round number shouldn't be used as a role model
        if (round.number() == 0) {
            generateInterface(round);
            generateRootClass(round);
            generateClass(round);
        }
    }

    private void generateInterface(Round round) {
        try (TypeGenerator typeGenerator =
                     round.getPackageOf(this.getClass()).openTypeGenerator("GeneratedInterface")) {
            typeGenerator.kind(INTERFACE);
            Type annotatedClass = annotatedClass(round);
            typeGenerator.addMethod("method0").returnType(annotatedClass);
        }
    }

    private void generateRootClass(Round round) {
        try (TypeGenerator typeGenerator = round.getRootPackage().openTypeGenerator("GeneratedRootClass")) {
            typeGenerator.kind(INTERFACE);
        }
    }

    private void generateClass(Round round) {
        try (TypeGenerator typeGenerator = round.getPackageOf(this.getClass()).openTypeGenerator("GeneratedClass")) {
            Type annotatedClass = annotatedClass(round);
            typeGenerator.addField("value").type(annotatedClass);
            typeGenerator.addMethod("method0").body("return value;").returnType(annotatedClass);
        }
    }

    private Type annotatedClass(Round round) {
        return round.typesAnnotatedWith(MarkerAnnotation.class).get(0);
    }
}
