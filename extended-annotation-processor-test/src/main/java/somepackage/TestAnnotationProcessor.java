package somepackage;

import static javax.lang.model.SourceVersion.*;

import java.io.*;

import javax.annotation.processing.SupportedSourceVersion;

import org.slf4j.*;

import com.github.t1.exap.*;
import com.github.t1.exap.reflection.Resource;

@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationClasses({ MarkerAnnotation.class })
public class TestAnnotationProcessor extends ExtendedAbstractProcessor {
    private static final Logger log = LoggerFactory.getLogger(TestAnnotationProcessor.class);

    @Override
    public boolean process(Round round) throws IOException {
        log.info("start annotation processor");
        Resource resource = round.getRootPackage().createResource("round-" + round.number());
        try (Writer writer = resource.openWriter()) {
            try (TestGenerator generator = new TestGenerator(writer)) {
                generator.write(round.typesAnnotatedWith(MarkerAnnotation.class));
            }
        }
        return false;
    }
}
