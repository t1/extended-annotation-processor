package somepackage;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface MultiAR {
    MultiA[] value();
}
