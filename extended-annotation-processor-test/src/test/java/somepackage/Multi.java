package somepackage;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface Multi {
    boolean booly();

    byte bytey();

    short shorty();

    int inty();

    long longy();

    float floaty();

    double doubly();

    char chary();

    String stringy();

    Class<?> classy();

    FooNum enumy();

    A annoty();
}
