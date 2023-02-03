package somepackage;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(MultiAR.class)
@Retention(RUNTIME)
public @interface MultiA {
    boolean[] booly();

    byte[] bytey();

    short[] shorty();

    int[] inty();

    long[] longy();

    float[] floaty();

    double[] doubly();

    char[] chary();

    String[] stringy();

    Class<?>[] classy();

    FooNum[] enumy();

    A[] annoty();
}
