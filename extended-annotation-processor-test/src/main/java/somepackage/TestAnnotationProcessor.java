package somepackage;

import static javax.lang.model.SourceVersion.*;
import static javax.tools.StandardLocation.*;

import java.io.*;

import javax.annotation.processing.SupportedSourceVersion;
import javax.tools.FileObject;

import org.slf4j.*;

import com.github.t1.exap.*;
import com.github.t1.exap.reflection.Type;

@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationClasses({ MarkerAnnotation.class })
public class TestAnnotationProcessor extends ExtendedAbstractProcessor {
    private static final Logger log = LoggerFactory.getLogger(TestAnnotationProcessor.class);

    @Override
    public boolean process(Round round) throws IOException {
        log.info("process {}", round);

        for (Type type : round.typesAnnotatedWith(MarkerAnnotation.class)) {
            type.warning("hi from round-" + round.number());
        }

        FileObject fileObject = filer().createResource(CLASS_OUTPUT, "", "round-" + round.number());
        log.debug("write {}", fileObject.getName());
        try (Writer writer = fileObject.openWriter()) {
            writer.write("");
        }

        return false;
    }
}
