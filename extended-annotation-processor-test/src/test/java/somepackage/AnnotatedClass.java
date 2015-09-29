package somepackage;

import java.util.Map;

/** javadoc summary. second sentence. */
@A("ppp")
@MarkerAnnotation
@SuppressWarnings("unused") // retention SOURCE
public class AnnotatedClass<T extends Number> {
    @A("fff")
    String string;
    boolean bool;
    Map<String, Number> map;

    @A("mmm")
    @A("nnn")
    public void method0() {}

    public String method1(String string, @A("ppp") boolean bool) {
        return null;
    }
}
