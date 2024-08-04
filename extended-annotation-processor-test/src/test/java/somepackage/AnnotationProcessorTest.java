package somepackage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationProcessorTest {
    private static final Path RESOURCES = Paths.get("src/test/resources");
    private static final Path TEST_CLASSES = Paths.get("target/test-classes");
    private static final Path GENERATED_TEST_SOURCES = Paths.get("target/generated-test-sources/test-annotations");

    @Test
    void verifyRound() {
        assertThat(contentOf(TEST_CLASSES.resolve("round-0.json")))
                .as("round-0")
                .isEqualTo(contentOf(RESOURCES.resolve("round-0.json")));
    }

    @Test
    void verifyFields() {
        assertThat(contentOf(TEST_CLASSES.resolve("fields-0")))
                .as("fields-0")
                .isEqualTo(contentOf(RESOURCES.resolve("fields-0")));
    }

    @Test
    void verifyPackages() {
        assertThat(contentOf(TEST_CLASSES.resolve("packages-0")))
                .as("packages-0")
                .isEqualTo(contentOf(RESOURCES.resolve("packages-0")));
    }

    @Test
    public void shouldHaveGeneratedInterface() {
        assertThat(contentOf(GENERATED_TEST_SOURCES.resolve("somepackage/GeneratedInterface.java"))).isEqualTo(
                "package somepackage;\n"
                + "\n"
                + "public interface GeneratedInterface {\n"
                + "\n"
                + "    AnnotatedClass method0();\n"
                + "}\n");
    }

    @Test
    public void shouldHaveGeneratedRootClass() {
        assertThat(contentOf(GENERATED_TEST_SOURCES.resolve("GeneratedRootClass.java"))).isEqualTo(
                "public interface GeneratedRootClass {\n"
                + "}\n");
    }

    @Test
    public void shouldHaveGeneratedClass() {
        assertThat(contentOf(GENERATED_TEST_SOURCES.resolve("somepackage/GeneratedClass.java"))).isEqualTo(
                "package somepackage;\n"
                + "\n"
                + "public class GeneratedClass {\n"
                + "    private AnnotatedClass value;\n"
                + "\n"
                + "    public AnnotatedClass method0() {\n"
                + "        return method1();\n"
                + "    }\n"
                + "\n"
                + "    private AnnotatedClass method1() throws RuntimeException {\n"
                + "        return value;\n"
                + "    }\n"
                + "}\n");
    }

    private static String contentOf(Path path) {
        return Assertions.contentOf(path.toFile()).trim() + "\n"; // maybe remove a leading newline
    }
}
