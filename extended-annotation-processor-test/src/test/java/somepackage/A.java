package somepackage;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(AA.class)
@Retention(RUNTIME)
public @interface A {
    String value();
}
