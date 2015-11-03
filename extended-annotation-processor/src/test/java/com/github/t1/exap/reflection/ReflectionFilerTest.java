package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;
import static javax.tools.StandardLocation.*;
import static org.assertj.core.api.StrictAssertions.*;

import java.io.*;

import javax.tools.StandardLocation;

import org.junit.*;

public class ReflectionFilerTest {
    @After
    public void clearMessages() {
        ENV.getCreatedResources().clear();
    }

    private void assertCreatedFile(String value, StandardLocation location, String pack, String name) {
        assertThat(ENV.getCreatedResource(location, pack, name)).isEqualTo(value);
    }

    @Test
    public void shouldCreateClassFile() throws IOException {
        class Pojo {}
        Type type = ReflectionType.type(Pojo.class);

        try (Writer writer = type.getPackage().createResource("foo").openWriter()) {
            writer.write("bar");
        } finally {
            assertCreatedFile("bar", CLASS_OUTPUT, getClass().getPackage().getName(), "foo");
        }
    }
}
