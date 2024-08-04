package somepackage;

import com.github.t1.exap.ExtendedAbstractProcessor;
import com.github.t1.exap.Round;
import com.github.t1.exap.SupportedAnnotationClasses;
import com.github.t1.exap.insight.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.SupportedSourceVersion;
import java.io.IOException;
import java.io.Writer;

import static com.github.t1.exap.generator.TypeKind.INTERFACE;
import static com.github.t1.exap.generator.Visibility.PACKAGE_PRIVATE;
import static com.github.t1.exap.generator.Visibility.PRIVATE;
import static com.github.t1.exap.generator.Visibility.PUBLIC;
import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
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
        createPackageInfos(round);
        generateSourceFile(round);
        return false;
    }

    private void createRoundOutput(Round round) throws IOException {
        var resource = round.getRootPackage().createResource("round-" + round.number() + ".json");
        try (var writer = resource.openWriter()) {
            try (var generator = new JsonTestFileGenerator(writer)) {
                generator.write(round.typesAnnotatedWith(MarkerAnnotation.class));
            }
        }
    }

    private void createFieldList(Round round) throws IOException {
        try (var writer = writerFor(round, "fields")) {
            for (var field : round.fieldsAnnotatedWith(MarkerAnnotation.class)) {
                writer.append(field.getName()).append(":").append(field.getAnnotation(MarkerAnnotation.class).value()).append("\n");
            }
        }
    }

    private void createPackageInfos(Round round) throws IOException {
        try (var writer = writerFor(round, "packages")) {
            for (var pkg : round.packagesAnnotatedWith(MarkerAnnotation.class)) {
                writer.append(pkg.getName()).append(": ").append(pkg.getAnnotation(MarkerAnnotation.class).value()).append("\n")
                        .append("javadoc: ").append(pkg.javaDoc().orElse("no javadoc")).append("\n");
            }
        }
    }

    private static Writer writerFor(Round round, String name) throws IOException {
        return round.getRootPackage().createResource(name + "-" + round.number()).openWriter();
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
        try (var typeGenerator =
                     round.getPackageOf(this.getClass()).openTypeGenerator("GeneratedInterface")) {
            typeGenerator.kind(INTERFACE);
            var annotatedClass = annotatedClass(round);
            typeGenerator.addMethod("method0").returnType(annotatedClass);
        }
    }

    private void generateRootClass(Round round) {
        try (var typeGenerator = round.getRootPackage().openTypeGenerator("GeneratedRootClass")) {
            typeGenerator.kind(INTERFACE);
        }
    }

    private void generateClass(Round round) {
        try (var typeGenerator = round.getPackageOf(this.getClass()).openTypeGenerator("GeneratedClass")) {
            var annotatedClass = annotatedClass(round);
            typeGenerator.addField("value").type(annotatedClass);
            typeGenerator.addMethod(PUBLIC, "method0")
                    .body("return method1();")
                    .returnType(annotatedClass);
            var method1 = typeGenerator
                    .addMethod(PACKAGE_PRIVATE, "method1")
                    .addThrows(ENV.type(RuntimeException.class))
                    .body("return value;");
            method1.visibility(PRIVATE) // cover this API
                    .returnType(annotatedClass);
        }
    }

    private Type annotatedClass(Round round) {
        return round.typesAnnotatedWith(MarkerAnnotation.class).get(0);
    }
}
