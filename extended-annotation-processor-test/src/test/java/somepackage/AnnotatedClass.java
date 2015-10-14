package somepackage;

import static somepackage.FooNum.*;

import java.util.*;

/** javadoc summary. second sentence. */
@A("ppp")
@MarkerAnnotation
@SuppressWarnings("unused") // retention SOURCE
public class AnnotatedClass<T extends Number> {
    boolean bool;

    @A("fff")
    String string;

    @FooNumA(X)
    @FooNums({ Y, Z })
    Map<String, Number> map;

    @Multi(booly = false, bytey = 0, chary = 'c', classy = Object.class, doubly = 0.0D, floaty = 0.0F, inty = 0,
            longy = 0, shorty = 0, string = "s")
    FooNum fooNum;

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
