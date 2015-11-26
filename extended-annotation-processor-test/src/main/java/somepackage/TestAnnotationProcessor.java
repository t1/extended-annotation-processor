package somepackage;

import static com.github.t1.exap.generator.TypeKind.*;
import static javax.lang.model.SourceVersion.*;

import java.io.*;

import javax.annotation.processing.SupportedSourceVersion;

import org.slf4j.*;

import com.github.t1.exap.*;
import com.github.t1.exap.generator.TypeGenerator;
import com.github.t1.exap.reflection.*;

@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationClasses({ MarkerAnnotation.class })
public class TestAnnotationProcessor extends ExtendedAbstractProcessor {
    private static final Logger log = LoggerFactory.getLogger(TestAnnotationProcessor.class);

    @Override
    public boolean process(Round round) throws IOException {
        log.info("start annotation processor");
        createRoundOutput(round);
        generateSourceFile(round);
        return false;
    }

    private void createRoundOutput(Round round) throws IOException {
        Resource resource = round.getRootPackage().createResource("round-" + round.number());
        try (Writer writer = resource.openWriter()) {
            try (TestGenerator generator = new TestGenerator(writer)) {
                generator.write(round.typesAnnotatedWith(MarkerAnnotation.class));
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
