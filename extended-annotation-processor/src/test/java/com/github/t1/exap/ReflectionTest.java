package com.github.t1.exap;

import static java.lang.annotation.RetentionPolicy.*;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.junit.Assert.*;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.github.t1.exap.reflection.*;

public class ReflectionTest {
    @Retention(RUNTIME)
    public @interface A {
        String value();
    }

    @A("ttt")
    @JavaDoc(summary = "s", value = "v")
    public static class Pojo {
        @A("fff")
        String string;
        boolean bool;

        @A("mmm")
        public void method0() {}

        @SuppressWarnings("unused")
        public String method1(String string, @A("ppp") boolean bool) {
            return null;
        }
    }

    private final Type type = Type.of(Pojo.class);

    @Test
    public void shouldGetType() {
        assertEquals(Pojo.class.getName(), type.getQualifiedName());
        assertEquals("Pojo", type.getSimpleName());
        assertFalse(type.isVoid());
        assertFalse(type.isBoolean());
        assertFalse(type.isNumber());
        assertFalse(type.isInteger());
        assertFalse(type.isDecimal());
        assertFalse(type.isString());
        assertFalse(type.isEnum());
        assertNull(type.getEnumValues());
        assertFalse(type.isArray());
        assertNull(type.elementType());
        assertTrue(type.getTypeParameters().isEmpty());
        assertFalse(type.isAssignableTo(String.class));

        assertTrue(type.isPublic());
        assertTrue(type.isStatic());
        assertFalse(type.isTransient()); // doesn't make sense, but must work

        assertTrue(type.isAnnotated(A.class));
        assertEquals("ttt", type.getAnnotation(A.class).value());
        assertEquals(2, type.getAnnotations().size());

        Annotation a0 = type.getAnnotations().get(0);
        assertEquals(A.class.getName(), a0.getAnnotationType().getQualifiedName());
        assertEquals(1, a0.getElementValues().size());
        assertEquals("ttt", a0.getElementValues().get("value"));

        Annotation a1 = type.getAnnotations().get(1);
        assertEquals(JavaDoc.class.getName(), a1.getAnnotationType().getQualifiedName());
        assertEquals(2, a1.getElementValues().size());
        assertEquals("s", a1.getElementValues().get("summary"));
        assertEquals("v", a1.getElementValues().get("value"));
    }

    @Test
    public void shouldGetFields() {
        List<Field> fields = type.getFields();

        assertEquals("fields size", 2, fields.size());

        Field stringField = fields.get(0);
        assertEquals("string", stringField.getName());
        assertEquals("java.lang.String", stringField.getType().getQualifiedName());
        assertTrue(stringField.isAnnotated(A.class));
        assertEquals("fff", stringField.getAnnotation(A.class).value());
        assertEquals(1, stringField.getAnnotations().size());

        Annotation a0 = stringField.getAnnotations().get(0);
        assertEquals(A.class.getName(), a0.getAnnotationType().getQualifiedName());
        assertEquals(1, a0.getElementValues().size());
        assertEquals("fff", a0.getElementValues().get("value"));

        assertFalse(stringField.isPublic());
        assertFalse(stringField.isStatic());
        assertFalse(stringField.isTransient());

        Field boolField = fields.get(1);
        assertEquals("bool", boolField.getName());
        assertEquals("boolean", boolField.getType().getQualifiedName());
        assertFalse(boolField.isAnnotated(A.class));
        assertNull(boolField.getAnnotation(A.class));
        assertEquals(0, boolField.getAnnotations().size());
    }

    @Test
    public void shouldGetMethods() {
        List<Method> methods = type.getMethods();

        assertEquals(2, methods.size());

        Method method0 = type.getMethod("method0");
        assertEquals("method0", method0.getName());
        assertEquals(type, method0.getContainerType());
        assertEquals(Type.of(void.class), method0.getReturnType());
        assertEquals(0, method0.getParameters().size());

        assertTrue(method0.isPublic());
        assertFalse(method0.isStatic());
        assertFalse(method0.isTransient());

        assertTrue(method0.isAnnotated(A.class));
        assertEquals("mmm", method0.getAnnotation(A.class).value());
        assertEquals(1, method0.getAnnotations().size());

        Annotation a0 = method0.getAnnotations().get(0);
        assertEquals(A.class.getName(), a0.getAnnotationType().getQualifiedName());
        assertEquals(1, a0.getElementValues().size());
        assertEquals("mmm", a0.getElementValues().get("value"));

        Method method1 = type.getMethod("method1");
        assertEquals("method1", method1.getName());
        assertEquals(type, method1.getContainerType());
        assertEquals(Type.of(String.class), method1.getReturnType());
        List<Parameter> parameters = method1.getParameters();

        assertEquals(2, parameters.size());

        Parameter parameter0 = parameters.get(0);
        assertEquals(method1, parameter0.getMethod());
        assertEquals("string", parameter0.getName());
        assertEquals(Type.of(String.class), parameter0.getType());

        Parameter parameter1 = parameters.get(1);
        assertEquals(method1, parameter1.getMethod());
        assertEquals("bool", parameter1.getName());
        assertEquals(Type.of(boolean.class), parameter1.getType());

        assertTrue(parameter1.isAnnotated(A.class));
        assertEquals("ppp", parameter1.getAnnotation(A.class).value());
        assertEquals(1, parameter1.getAnnotations().size());

        Annotation p1a0 = parameter1.getAnnotations().get(0);
        assertEquals(A.class.getName(), p1a0.getAnnotationType().getQualifiedName());
        assertEquals(1, p1a0.getElementValues().size());
        assertEquals("ppp", p1a0.getElementValues().get("value"));
    }

    @Test
    public void shouldVisitType() {
        AtomicInteger count = new AtomicInteger();

        type.accept(new TypeVisitor() {
            @Override
            public void visit(Method method) {
                assertThat(method.getName()).matches("method[01]");
                count.getAndIncrement();
            }

            @Override
            public void visit(Field field) {
                assertThat(field.getName()).matches("string|bool");
                count.getAndIncrement();
            }
        });

        assertEquals(4, count.get());
    }
}
