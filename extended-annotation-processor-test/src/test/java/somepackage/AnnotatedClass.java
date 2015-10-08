package somepackage;

import java.util.*;

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
    @B("bbb")
    public void method0() {}

    @A("ooo")
    @BB({ @B("b0"), @B("b1") })
    public List<String> method1(String string, @A("ppp") boolean bool, List<String> strings) {
        return null;
    }
}
