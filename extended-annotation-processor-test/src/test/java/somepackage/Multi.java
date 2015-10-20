package somepackage;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

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

    A annoty();

    FooNum enumy();
}
