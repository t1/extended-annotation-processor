package somepackage;

import static org.assertj.core.api.Assertions.*;

import java.nio.file.*;

import org.junit.Test;

public class AnnotationProcessorIT {
    private static final Path EXPECTED_FOLDER = Paths.get("src/test/resources");
    private static final Path ACTUAL_FOLDER = Paths.get("target/test-classes");

    @Test
    public void shouldHaveRunRound0() {
        assertRound(0);
    }

    @Test
    public void shouldHaveRunRound1() {
        assertRound(1);
    }

    private void assertRound(int round) {
        assertThat(ACTUAL_FOLDER.resolve("round-" + round)).hasSameContentAs(EXPECTED_FOLDER.resolve("round-" + round));
    }
}
