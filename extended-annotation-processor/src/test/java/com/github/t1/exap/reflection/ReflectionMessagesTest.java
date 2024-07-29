package com.github.t1.exap.reflection;

import com.github.t1.exap.insight.AnnotationWrapper;
import com.github.t1.exap.insight.Elemental;
import com.github.t1.exap.insight.Field;
import com.github.t1.exap.insight.Message;
import com.github.t1.exap.insight.Method;
import com.github.t1.exap.insight.Parameter;
import com.github.t1.exap.insight.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.tools.Diagnostic.Kind;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.ENV;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.OTHER;
import static javax.tools.Diagnostic.Kind.WARNING;
import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionMessagesTest {
    @AfterEach
    public void clearMessages() {
        ENV.getMessages().clear();
    }

    private void assertMessage(Elemental elemental, Kind kind, String message) {
        assertThat(ENV.getMessages()).containsExactly(new Message(elemental, kind, message));
        assertThat(ENV.getMessages(elemental, kind)).containsExactly(message);
    }

    @Test
    public void shouldMarkType() {
        class Pojo {}
        Type type = ENV.type(Pojo.class);

        type.warning("foo");

        assertMessage(type, WARNING, "foo");
    }

    @Test
    public void shouldMarkTypeAnnotation() {
        @SuppressWarnings("DeprecatedIsStillUsed") @Deprecated
        class Pojo {}
        AnnotationWrapper annotation = ENV.type(Pojo.class).getAnnotationWrapper(Deprecated.class);

        annotation.error("bar");

        assertMessage(annotation, ERROR, "bar");
    }

    @Test
    public void shouldMarkField() {
        class Pojo {
            @SuppressWarnings("unused")
            String field;
        }
        Field field = ENV.type(Pojo.class).getField("field");

        field.note("baz");

        assertMessage(field, NOTE, "baz");
    }

    @Test
    public void shouldMarkFieldAnnotation() {
        class Pojo {
            @Deprecated
            @SuppressWarnings("unused")
            String field;
        }
        AnnotationWrapper annotation = ENV.type(Pojo.class).getField("field").getAnnotationWrapper(Deprecated.class);

        annotation.mandatoryWarning("foo");

        assertMessage(annotation, MANDATORY_WARNING, "foo");
    }

    @Test
    public void shouldMarkMethod() {
        class Pojo {
            @SuppressWarnings("unused")
            public void method() {}
        }
        Method method = ENV.type(Pojo.class).getMethod("method");

        method.otherMessage("foo");

        assertMessage(method, OTHER, "foo");
    }

    @Test
    public void shouldMarkMethodAnnotation() {
        class Pojo {
            @Deprecated
            @SuppressWarnings("unused")
            public void method() {}
        }
        AnnotationWrapper annotation = ENV.type(Pojo.class).getMethod("method").getAnnotationWrapper(Deprecated.class);

        annotation.warning("foo");

        assertMessage(annotation, WARNING, "foo");
    }

    @Test
    public void shouldMarkParameter() {
        class Pojo {
            @SuppressWarnings("unused")
            public void method(String param) {}
        }
        Parameter parameter = ENV.type(Pojo.class).getMethod("method").getParameter(0);

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
                ENV.type(Pojo.class).getMethod("method").getParameter(0).getAnnotationWrapper(Deprecated.class);

        annotation.warning("foo");

        assertMessage(annotation, WARNING, "foo");
    }
}
