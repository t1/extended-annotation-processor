package somepackage;

import static javax.lang.model.SourceVersion.*;
import static javax.tools.StandardLocation.*;

import java.io.*;

import javax.annotation.processing.SupportedSourceVersion;
import javax.tools.FileObject;

import org.slf4j.*;

import com.github.t1.exap.*;

@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationClasses({ MarkerAnnotation.class })
public class TestAnnotationProcessor extends ExtendedAbstractProcessor {
    private static final Logger log = LoggerFactory.getLogger(TestAnnotationProcessor.class);

    @Override
    public boolean process(Round round) throws IOException {
        log.info("start annotation processor");
        FileObject fileObject = filer().createResource(CLASS_OUTPUT, "", "round-" + round.number());
        try (Writer writer = fileObject.openWriter()) {
            try (TestGenerator generator = new TestGenerator(writer)) {
                generator.write(round.typesAnnotatedWith(MarkerAnnotation.class));
            }
        }
        return false;
    }
}
