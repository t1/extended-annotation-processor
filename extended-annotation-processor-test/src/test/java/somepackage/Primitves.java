package somepackage;

/** javadoc summary. second sentence. */
@MarkerAnnotation
public class Primitves {
    @A("fff")
    String string;
    boolean bool;

    @A("mmm")
    public void method0() {}

    @SuppressWarnings("unused")
    public String method1(String string, @A("ppp") boolean bool) {
        return null;
    }
}
