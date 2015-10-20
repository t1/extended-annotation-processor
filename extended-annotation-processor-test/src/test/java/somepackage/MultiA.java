package somepackage;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

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

    A[] annoty();

    FooNum[] enumy();
}
