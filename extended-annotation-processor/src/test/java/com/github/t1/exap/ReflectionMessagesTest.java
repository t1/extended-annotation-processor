package com.github.t1.exap;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;
import static javax.tools.Diagnostic.Kind.*;
import static org.assertj.core.api.Assertions.*;

import javax.tools.Diagnostic.Kind;

import org.junit.*;

import com.github.t1.exap.reflection.*;

public class ReflectionMessagesTest {
    @After
    public void clearMessages() {
        ENV.getMessages().clear();
    }

    private void assertMessage(Elemental elemental, Kind kind, String message) {
        assertThat(ENV.getMessages()).containsExactly(new Message(elemental, kind, message));
    }

    @Test
    public void shouldMarkType() {
        class Pojo {}
        Type type = Type.of(Pojo.class);

        type.warning("foo");

        assertMessage(type, WARNING, "foo");
    }

    @Test
    public void shouldMarkTypeAnnotation() {
        @Deprecated
        class Pojo {}
        AnnotationWrapper annotation = Type.of(Pojo.class).getAnnotationWrapper(Deprecated.class);

        annotation.warning("foo");

        assertMessage(annotation, WARNING, "foo");
    }

    @Test
    public void shouldMarkField() {
        class Pojo {
            @SuppressWarnings("unused")
            String field;
        }
        Field field = Type.of(Pojo.class).getField("field");

        field.warning("foo");

        assertMessage(field, WARNING, "foo");
    }

    @Test
    public void shouldMarkFieldAnnotation() {
        class Pojo {
            @Deprecated
            @SuppressWarnings("unused")
            String field;
        }
        AnnotationWrapper annotation = Type.of(Pojo.class).getField("field").getAnnotationWrapper(Deprecated.class);

        annotation.warning("foo");

        assertMessage(annotation, WARNING, "foo");
    }

    @Test
    public void shouldMarkMethod() {
        class Pojo {
            @SuppressWarnings("unused")
            public void method() {}
        }
        Method method = Type.of(Pojo.class).getMethod("method");

        method.warning("foo");

        assertMessage(method, WARNING, "foo");
    }

    @Test
    public void shouldMarkMethodAnnotation() {
        class Pojo {
            @Deprecated
            @SuppressWarnings("unused")
            public void method() {}
        }
        AnnotationWrapper annotation = Type.of(Pojo.class).getMethod("method").getAnnotationWrapper(Deprecated.class);

        annotation.warning("foo");

        assertMessage(annotation, WARNING, "foo");
    }

    @Test
    public void shouldMarkParameter() {
        class Pojo {
            @SuppressWarnings("unused")
            public void method(String param) {}
        }
        Parameter parameter = Type.of(Pojo.class).getMethod("method").getParameter(0);

        parameter.warning("foo");

        assertMessage(parameter, WARNING, "foo");
    }

    @Test
    public void shouldMarkParameterAnnotation() {
        class Pojo {
            @SuppressWarnings("unused")
            public void method(@Deprecated String param) {}
        }
        AnnotationWrapper annotation =
                Type.of(Pojo.class).getMethod("method").getParameter(0).getAnnotationWrapper(Deprecated.class);

        annotation.warning("foo");

        assertMessage(annotation, WARNING, "foo");
    }
}
