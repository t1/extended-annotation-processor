package somepackage;

/** javadoc summary. second sentence. */
@A("ppp")
@MarkerAnnotation
@SuppressWarnings("unused") // retention SOURCE
public class Primitves<T extends Number> {
    @A("fff")
    String string;
    boolean bool;

    @A("mmm")
    public void method0() {}

    public String method1(String string, @A("ppp") boolean bool) {
        return null;
    }
}
