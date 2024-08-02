package somepackage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationProcessorTest {
    private static final Path RESOURCES = Paths.get("src/test/resources");
    private static final Path TEST_CLASSES = Paths.get("target/test-classes");
    private static final Path GENERATED_TEST_SOURCES = Paths.get("target/generated-test-sources/test-annotations");

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void verifyRoundHasRun(int round) {
        assertThat(contentOf(TEST_CLASSES, "round-" + round + ".json"))
                .as("round-" + round)
                .isEqualTo(contentOf(RESOURCES, "round-" + round + ".json"));
        assertThat(contentOf(TEST_CLASSES, "fields-" + round))
                .as("fields-" + round)
                .isEqualTo(contentOf(RESOURCES, "fields-" + round));
    }

    private static String contentOf(Path testClasses, String round) {
        return Assertions.contentOf(testClasses.resolve(round).toFile()).trim();
    }

    @Test
    public void shouldHaveGeneratedInterface() {
        assertThat(GENERATED_TEST_SOURCES.resolve("somepackage/GeneratedInterface.java")).hasContent(
                "package somepackage;\n"
                + "\n"
                + "public interface GeneratedInterface {\n"
                + "    public AnnotatedClass method0();\n"
                + "\n"
                + "}\n");
    }

    @Test
    public void shouldHaveGeneratedRootClass() {
        assertThat(GENERATED_TEST_SOURCES.resolve("GeneratedRootClass.java")).hasContent(
                "public interface GeneratedRootClass {\n"
                + "}\n");
    }

    @Test
    public void shouldHaveGeneratedClass() {
        assertThat(GENERATED_TEST_SOURCES.resolve("somepackage/GeneratedClass.java")).hasContent(
                "package somepackage;\n"
                + "\n"
                + "public class GeneratedClass {\n"
                + "    private AnnotatedClass value;\n"
                + "\n"
                + "    public AnnotatedClass method0() {\n"
                + "        return value;\n"
                + "    }\n"
                + "\n"
                + "}\n");
    }
}
