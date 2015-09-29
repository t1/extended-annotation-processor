package com.github.t1.exap;

import static java.lang.annotation.RetentionPolicy.*;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.Assert.*;

import java.lang.annotation.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.github.t1.exap.reflection.*;

public class ReflectionTest {
    @Repeatable(AA.class)
    @Retention(RUNTIME)
    public @interface A {
        String value();
    }

    @Retention(RUNTIME)
    public @interface AA {
        A[]value();
    }

    @A("ttt")
    @JavaDoc(summary = "s", value = "v")
    public static class Pojo {
        @A("fff")
        String string;
        boolean bool;

        @A("mmm")
        @A("nnn")
        public void method0() {}

        @SuppressWarnings("unused")
        public String method1(String string, @A("ppp") boolean bool) {
            return null;
        }
    }

    private final Type type = Type.of(Pojo.class);

    @Test
    public void shouldGetType() {
        assertType();
        assertTypeAnnotations();
    }

    private void assertType() {
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
        assertFalse(type.isTransient()); // doesn't make sense, but must not lie
    }

    private void assertTypeAnnotations() {
        assertTrue(type.isAnnotated(A.class));
        assertEquals("ttt", type.getAnnotation(A.class).value());
        assertEquals(1, type.getAnnotationWrappers(A.class).size());
        assertEquals("ttt", type.getAnnotationWrappers(A.class).get(0).get("value"));

        assertTrue(type.isAnnotated(JavaDoc.class));
        assertEquals(1, type.getAnnotationWrappers(JavaDoc.class).size());
        assertEquals("s", type.getAnnotationWrappers(JavaDoc.class).get(0).get("summary"));
        assertEquals("v", type.getAnnotationWrappers(JavaDoc.class).get(0).get("value"));

        List<AnnotationWrapper> wrappers = type.getAnnotationWrappers();
        assertEquals(2, wrappers.size());

        AnnotationWrapper a0 = wrappers.get(0);
        assertEquals(A.class.getName(), a0.getAnnotationType().getQualifiedName());
        assertEquals(1, a0.getElementValues().size());
        assertEquals("ttt", a0.getElementValues().get("value"));

        AnnotationWrapper a1 = wrappers.get(1);
        assertEquals(JavaDoc.class.getName(), a1.getAnnotationType().getQualifiedName());
        assertEquals(2, a1.getElementValues().size());
        assertEquals("s", a1.getElementValues().get("summary"));
        assertEquals("v", a1.getElementValues().get("value"));
    }

    @Test
    public void shouldGetFields() {
        List<Field> fields = type.getFields();

        assertEquals("fields size", 2, fields.size());
        assertStringField(fields.get(0));
        assertBoolField(fields.get(1));
    }

    private void assertStringField(Field stringField) {
        assertEquals("string", stringField.getName());
        assertEquals("java.lang.String", stringField.getType().getQualifiedName());
        assertFalse(stringField.isPublic());
        assertFalse(stringField.isStatic());
        assertFalse(stringField.isTransient()); // doesn't make sense, but must not lie

        assertTrue(stringField.isAnnotated(A.class));
        assertEquals("fff", stringField.getAnnotation(A.class).value());
        assertEquals(1, stringField.getAnnotationWrappers(A.class).size());
        assertEquals("fff", stringField.getAnnotationWrappers(A.class).get(0).get("value"));

        assertEquals(1, stringField.getAnnotationWrappers().size());

        AnnotationWrapper a0 = stringField.getAnnotationWrappers().get(0);
        assertEquals(A.class.getName(), a0.getAnnotationType().getQualifiedName());
        assertEquals(1, a0.getElementValues().size());
        assertEquals("fff", a0.getElementValues().get("value"));
    }

    private void assertBoolField(Field boolField) {
        assertEquals("bool", boolField.getName());
        assertEquals("boolean", boolField.getType().getQualifiedName());
        assertFalse(boolField.isAnnotated(A.class));
        assertNull(boolField.getAnnotation(A.class));
        assertEquals(0, boolField.getAnnotationWrappers(A.class).size());
        assertEquals(0, boolField.getAnnotationWrappers().size());
    }

    @Test
    public void shouldGetMethods() {
        List<Method> methods = type.getMethods();

        assertEquals(2, methods.size());
        assertMethod0(type.getMethod("method0"));
        assertMethod1(type.getMethod("method1"));
    }

    private void assertMethod0(Method method) {
        assertEquals("method0", method.getName());
        assertEquals(type, method.getContainerType());
        assertEquals(Type.of(void.class), method.getReturnType());
        assertTrue(method.isPublic());
        assertFalse(method.isStatic());
        assertFalse(method.isTransient());

        assertEquals(0, method.getParameters().size());

        assertMethod0Annotations(method);
    }

    private void assertMethod0Annotations(Method method) {
        assertFalse(method.isAnnotated(A.class));
        assertTrue(method.isAnnotated(AA.class));
        assertEquals(2, method.getAnnotation(AA.class).value().length);
        assertEquals("mmm", method.getAnnotation(AA.class).value()[0].value());
        assertEquals("nnn", method.getAnnotation(AA.class).value()[1].value());

        assertEquals(2, method.getAnnotationWrappers(A.class).size());
        assertEquals("mmm", method.getAnnotationWrappers(A.class).get(0).get("value"));
        assertEquals("nnn", method.getAnnotationWrappers(A.class).get(1).get("value"));
        assertThat(catchThrowable(() -> method.getAnnotationWrapper(A.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Found 2 annotations of type " + A.class.getName() + " when expecting only one");

        assertEquals(2, method.getAnnotationWrappers().size());
        assertRepeatedAnnotation("mmm", method.getAnnotationWrappers().get(0));
        assertRepeatedAnnotation("nnn", method.getAnnotationWrappers().get(1));
    }

    private void assertRepeatedAnnotation(String value, AnnotationWrapper annotation) {
        assertEquals(A.class.getName(), annotation.getAnnotationType().getQualifiedName());
        assertEquals(1, annotation.getElementValues().size());
        assertEquals(value, annotation.getElementValues().get("value"));
    }

    private void assertMethod1(Method method) {
        assertEquals("method1", method.getName());
        assertEquals(type, method.getContainerType());
        assertEquals(Type.of(String.class), method.getReturnType());
        List<Parameter> parameters = method.getParameters();

        // @SuppressWarnings is RetentionPolicy.SOURCE, so it's not visible in reflection
        assertEquals(0, method.getAnnotationWrappers().size());

        assertEquals(2, parameters.size());
        assertMethod1Parameter0(method, parameters.get(0));
        assertMethod1Parameter1(method, parameters.get(1));
    }

    private void assertMethod1Parameter0(Method method, Parameter parameter) {
        assertEquals(method, parameter.getMethod());
        assertEquals("string", parameter.getName());
        assertEquals(Type.of(String.class), parameter.getType());

        assertFalse(parameter.isAnnotated(A.class));
        assertNull(parameter.getAnnotation(A.class));
        assertEquals(0, parameter.getAnnotationWrappers().size());
    }

    private void assertMethod1Parameter1(Method method, Parameter parameter1) {
        assertEquals(method, parameter1.getMethod());
        assertEquals("bool", parameter1.getName());
        assertEquals(Type.of(boolean.class), parameter1.getType());

        assertTrue(parameter1.isAnnotated(A.class));
        assertEquals("ppp", parameter1.getAnnotation(A.class).value());
        assertEquals(1, parameter1.getAnnotationWrappers().size());

        AnnotationWrapper p1a0 = parameter1.getAnnotationWrappers().get(0);
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
