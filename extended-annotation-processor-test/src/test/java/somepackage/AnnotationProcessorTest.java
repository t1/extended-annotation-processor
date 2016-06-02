package somepackage;

import org.junit.Test;

import java.nio.file.*;

import static org.assertj.core.api.Assertions.*;

public class AnnotationProcessorTest {
    private static final Path RESOURCES = Paths.get("src/test/resources");
    private static final Path TEST_CLASSES = Paths.get("target/test-classes");
    private static final Path GENERATED_TEST_SOURCES = Paths.get("target/generated-test-sources/test-annotations");

    @Test
    public void shouldHaveRunRound0() {
        verifyRoundHasRun(0);
    }

    @Test
    public void shouldHaveRunRound1() {
        verifyRoundHasRun(1);
    }

    private void verifyRoundHasRun(int round) {
        assertThat(TEST_CLASSES.resolve("round-" + round)).hasSameContentAs(RESOURCES.resolve("round-" + round));
        assertThat(TEST_CLASSES.resolve("fields-" + round)).hasSameContentAs(RESOURCES.resolve("fields-" + round));
    }

    @Test
    public void shouldHaveGeneratedInterface() {
        assertThat(GENERATED_TEST_SOURCES.resolve("somepackage/GeneratedInterface.java")).hasContent(""
                + "package somepackage;\n"
                + "\n"
                + "import somepackage.AnnotatedClass;\n"
                + "\n"
                + "public interface GeneratedInterface {\n"
                + "    public AnnotatedClass method0();\n"
                + "\n"
                + "}\n");
    }

    @Test
    public void shouldHaveGeneratedRootClass() {
        assertThat(GENERATED_TEST_SOURCES.resolve("GeneratedRootClass.java")).hasContent(""
                + "public interface GeneratedRootClass {\n"
                + "}\n");
    }

    @Test
    public void shouldHaveGeneratedClass() {
        assertThat(GENERATED_TEST_SOURCES.resolve("somepackage/GeneratedClass.java")).hasContent(""
                + "package somepackage;\n"
                + "\n"
                + "import somepackage.AnnotatedClass;\n"
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
