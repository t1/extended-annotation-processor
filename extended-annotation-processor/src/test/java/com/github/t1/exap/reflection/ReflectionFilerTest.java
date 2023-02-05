package com.github.t1.exap.reflection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class ReflectionFilerTest {
    @AfterEach
    public void clearMessages() {
        ENV.getCreatedResources().clear();
    }

    @Test
    public void shouldCreateClassFile() throws IOException {
        class Pojo {}
        Type type = ENV.type(Pojo.class);

        try (Writer writer = type.getPackage().createResource("foo").openWriter()) {
            writer.write("bar");
        }
        assertThat(ENV.getCreatedResource(CLASS_OUTPUT, getClass().getPackage().getName(), "foo")).isEqualTo("bar");
    }
}
