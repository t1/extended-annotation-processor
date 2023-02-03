package somepackage;

import somepackage.Container.Nested;

import java.util.List;
import java.util.Map;

import static somepackage.FooNum.X;
import static somepackage.FooNum.Y;
import static somepackage.FooNum.Z;

/** javadoc summary. second sentence. */
@A("ppp")
@MarkerAnnotation("on type")
@SuppressWarnings("unused") // retention SOURCE
public class AnnotatedClass<T extends Number> {
    public static final String CONSTANT = "dummy";

    public static String constant() {
        return CONSTANT;
    }

    boolean bool;

    /** field javadoc */
    @A("fff")
    @MarkerAnnotation("on field")
    String string;

    @FooNumA(X)
    @FooNums({Y, Z})
    Map<String, Number> map;

    @Multi(booly = false, bytey = 1, chary = 0x21, classy = Object.class, doubly = 3.4D, floaty = 5.0F, inty = 7,
        longy = 8, shorty = 9, stringy = "s", enumy = X, annoty = @A("a"))
    @MultiA(booly = {}, bytey = {}, chary = {}, classy = {}, doubly = {}, floaty = {}, inty = {}, longy = {},
        shorty = {}, stringy = {}, enumy = {}, annoty = {})
    @MultiA(booly = false, bytey = 1, chary = 0x21, classy = Object.class, doubly = 3.4D, floaty = 5.0F, inty = 7,
        longy = 8, shorty = 9, stringy = "s", enumy = X, annoty = @A("a"))
    @MultiA(booly = {false, true}, bytey = {1, 2}, chary = {0x21, 0x22}, classy = {Object.class, String.class},
        doubly = {3.4D, 4.3D}, floaty = {5.0F, 6.5F}, inty = {7, 8}, longy = {8, 9}, shorty = {9, 8},
        stringy = {"s", "t"}, enumy = {X, Y}, annoty = {@A("a"), @A("b")})
    FooNum fooNum;

    /** method javadoc */
    @A("mmm")
    @A("nnn")
    @B("bbb")
    @MarkerAnnotation("on method")
    public void method0() {}

    @A("ooo")
    @BB({@B("b0"), @B("b1")})
    public List<String> method1(String string, @MarkerAnnotation("on param") @A("ppp") boolean bool,
                                List<String> strings) {
        return null;
    }

    public Nested method2() {
        return null;
    }
}
