package somepackage;

import static javax.lang.model.SourceVersion.*;
import static javax.tools.StandardLocation.*;

import java.io.*;

import javax.annotation.processing.SupportedSourceVersion;
import javax.tools.FileObject;

import com.github.t1.exap.*;
import com.github.t1.exap.reflection.*;

@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationClasses({ MarkerAnnotation.class })
public class TestAnnotationProcessor extends ExtendedAbstractProcessor {
    @Override
    public boolean process(Round round) throws IOException {
        FileObject fileObject = filer().createResource(CLASS_OUTPUT, "", "round-" + round.number());
        try (Writer writer = fileObject.openWriter()) {
            PrintWriter out = new PrintWriter(writer);

            out.println(MarkerAnnotation.class.getName() + ":");
            for (Type type : round.typesAnnotatedWith(MarkerAnnotation.class)) {
                type.warning("marked warning in round-" + round.number());
                out.println("- " + type.getQualifiedName());
                out.println("    javadoc:");
                out.println("        summary: " + type.getAnnotation(JavaDoc.class).summary());
                out.println("        value: " + type.getAnnotation(JavaDoc.class).value());

                out.println("    fields:");
                for (Field field : type.getFields())
                    out.println("    - " + field.getName());

                out.println("    methods:");
                for (Method method : type.getMethods())
                    out.println("    - " + method.getName());
            }
        }
        return false;
    }
}
