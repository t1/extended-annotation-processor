package com.github.t1.exap;

import static java.lang.annotation.RetentionPolicy.*;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.Assert.*;

import java.lang.annotation.*;
import java.util.*;
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
        A[] value();
    }

    @Retention(RUNTIME)
    public @interface B {
        String value();
    }

    @Retention(RUNTIME)
    public @interface BB {
        B[] value();
    }

    @A("ttt")
    @JavaDoc(summary = "s", value = "v")
    public static class Pojo {
        @A("fff")
        String string;
        boolean bool;
        Map<String, Number> map;

        @A("mmm")
        @A("nnn")
        @B("bbb")
        public void method0() {}

        @A("ooo")
        @BB({ @B("b0"), @B("b1") })
        @SuppressWarnings("unused")
        public String method1(String string, @A("ppp") boolean bool, List<String> strings) {
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
        assertEquals("Pojo", type.getSimpleName());
        assertEquals(Pojo.class.getName(), type.getFullName());
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
        assertFalse(type.isA(String.class));

        assertTrue(type.isPublic());
        assertTrue(type.isStatic());
        assertFalse(type.isTransient()); // doesn't make sense, but must not lie
    }

    private void assertTypeAnnotations() {
        assertTrue(type.isAnnotated(A.class));
        assertEquals(1, type.getAnnotations(A.class).size());
        assertEquals("ttt", type.getAnnotations(A.class).get(0).value());
        assertEquals(1, type.getAnnotationWrappers(A.class).size());
        assertEquals("ttt", type.getAnnotationWrappers(A.class).get(0).getValue());

        assertTrue(type.isAnnotated(JavaDoc.class));
        assertEquals(1, type.getAnnotationWrappers(JavaDoc.class).size());
        assertEquals("s", type.getAnnotationWrappers(JavaDoc.class).get(0).getValue("summary"));
        assertEquals("v", type.getAnnotationWrappers(JavaDoc.class).get(0).getValue());

        List<AnnotationWrapper> wrappers = type.getAnnotationWrappers();
        assertEquals(2, wrappers.size());

        AnnotationWrapper a0 = wrappers.get(0);
        assertEquals("A", a0.getAnnotationType().getSimpleName());
        assertEquals(A.class.getName(), a0.getAnnotationType().getFullName());
        assertEquals(1, a0.getElementValues().size());
        assertEquals("ttt", a0.getElementValues().get("value"));

        AnnotationWrapper a1 = wrappers.get(1);
        assertEquals("JavaDoc", a1.getAnnotationType().getSimpleName());
        assertEquals(JavaDoc.class.getName(), a1.getAnnotationType().getFullName());
        assertEquals(2, a1.getElementValues().size());
        assertEquals("s", a1.getElementValues().get("summary"));
        assertEquals("v", a1.getElementValues().get("value"));
    }

    @Test
    public void shouldGetFields() {
        List<Field> fields = type.getFields();

        assertEquals("fields size", 3, fields.size());
        assertStringField(fields.get(0));
        assertBoolField(fields.get(1));
        assertMapField(fields.get(2));
    }

    private void assertStringField(Field stringField) {
        assertEquals("string", stringField.getName());
        assertEquals("String", stringField.getType().getSimpleName());
        assertEquals("java.lang.String", stringField.getType().getFullName());
        assertFalse(stringField.isPublic());
        assertFalse(stringField.isStatic());
        assertFalse(stringField.isTransient()); // doesn't make sense, but must not lie

        assertTrue(stringField.isAnnotated(A.class));
        assertEquals(1, stringField.getAnnotations(A.class).size());
        assertEquals("fff", stringField.getAnnotations(A.class).get(0).value());
        assertEquals(1, stringField.getAnnotationWrappers(A.class).size());
        assertEquals("fff", stringField.getAnnotationWrappers(A.class).get(0).getValue());

        assertEquals(1, stringField.getAnnotationWrappers().size());

        AnnotationWrapper a0 = stringField.getAnnotationWrappers().get(0);
        assertEquals("A", a0.getAnnotationType().getSimpleName());
        assertEquals(A.class.getName(), a0.getAnnotationType().getFullName());
        assertEquals(1, a0.getElementValues().size());
        assertEquals("fff", a0.getElementValues().get("value"));
    }

    private void assertBoolField(Field boolField) {
        assertEquals("bool", boolField.getName());
        assertEquals("boolean", boolField.getType().getSimpleName());
        assertEquals("boolean", boolField.getType().getFullName());
        assertFalse(boolField.isAnnotated(A.class));
        assertEquals(0, boolField.getAnnotations(A.class).size());
        assertEquals(0, boolField.getAnnotationWrappers(A.class).size());
        assertEquals(0, boolField.getAnnotationWrappers().size());
    }

    private void assertMapField(Field mapField) {
        assertEquals("map", mapField.getName());
        assertEquals("Map", mapField.getType().getSimpleName());
        assertEquals("java.util.Map<java.lang.String, java.lang.Number>", mapField.getType().getFullName());
        assertTrue(mapField.getType().isA(Map.class));
        assertEquals(2, mapField.getType().getTypeParameters().size());
        assertEquals("String", mapField.getType().getTypeParameters().get(0).getSimpleName());
        assertEquals("java.lang.String", mapField.getType().getTypeParameters().get(0).getFullName());
        assertEquals("Number", mapField.getType().getTypeParameters().get(1).getSimpleName());
        assertEquals("java.lang.Number", mapField.getType().getTypeParameters().get(1).getFullName());

        assertFalse(mapField.isAnnotated(A.class));
        assertEquals(0, mapField.getAnnotations(A.class).size());
        assertEquals(0, mapField.getAnnotationWrappers(A.class).size());
        assertEquals(0, mapField.getAnnotationWrappers().size());
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
        assertTrue(method.isAnnotated(AA.class)); // TODO shouldn't we 'hide' the repeater?
        assertEquals(2, method.getAnnotation(AA.class).value().length);
        assertEquals(1, method.getAnnotations(AA.class).size());
        assertEquals(1, method.getAnnotationWrappers(AA.class).size());
        assertEquals(2, ((A[]) method.getAnnotationWrapper(AA.class).getValue()).length);

        assertTrue(method.isAnnotated(A.class));
        assertEquals(2, method.getAnnotations(A.class).size());
        assertEquals("mmm", method.getAnnotations(A.class).get(0).value());
        assertEquals("nnn", method.getAnnotations(A.class).get(1).value());

        assertEquals(2, method.getAnnotationWrappers(A.class).size());
        assertEquals("mmm", method.getAnnotationWrappers(A.class).get(0).getValue());
        assertEquals("nnn", method.getAnnotationWrappers(A.class).get(1).getValue());
        assertThat(catchThrowable(() -> method.getAnnotationWrapper(A.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Found 2 annotations of type " + A.class.getName() + " when expecting only one");

        assertTrue(method.isAnnotated(B.class));
        assertEquals(1, method.getAnnotations(B.class).size());
        assertEquals("bbb", method.getAnnotations(B.class).get(0).value());
        assertEquals(1, method.getAnnotationWrappers(B.class).size());
        assertEquals("bbb", method.getAnnotationWrappers(B.class).get(0).getStringValue());

        assertEquals(3, method.getAnnotationWrappers().size());
        assertRepeatedAnnotation("mmm", method.getAnnotationWrappers().get(0), A.class);
        assertRepeatedAnnotation("nnn", method.getAnnotationWrappers().get(1), A.class);
        assertRepeatedAnnotation("bbb", method.getAnnotationWrappers().get(2), B.class);
    }

    private void assertRepeatedAnnotation(String value, AnnotationWrapper annotation, Class<?> type) {
        assertEquals(type.getSimpleName(), annotation.getAnnotationType().getSimpleName());
        assertEquals(type.getName(), annotation.getAnnotationType().getFullName());
        assertEquals(1, annotation.getElementValues().size());
        assertEquals(value, annotation.getElementValues().get("value"));
    }

    private void assertMethod1(Method method) {
        assertEquals("method1", method.getName());
        assertEquals(type, method.getContainerType());
        assertEquals(Type.of(String.class), method.getReturnType());

        assertMethod1Annotations(method);

        List<Parameter> parameters = method.getParameters();
        assertMethod1Parameters(method, parameters);
    }

    private void assertMethod1Annotations(Method method) {
        // @SuppressWarnings is RetentionPolicy.SOURCE, so it's not visible in reflection
        assertEquals(2, method.getAnnotationWrappers().size());
        assertMethod1AnnotationA(method);
        assertMethod1AnnotationBB(method);
    }

    private void assertMethod1AnnotationA(Method method) {
        assertTrue(method.isAnnotated(A.class));
        assertEquals(1, method.getAnnotations(A.class).size());
        assertEquals("ooo", method.getAnnotation(A.class).value());
        assertEquals(1, method.getAnnotationWrappers(A.class).size());
        assertEquals("ooo", method.getAnnotationWrappers(A.class).get(0).getStringValue());
        assertFalse(method.isAnnotated(AA.class));
        assertEquals(0, method.getAnnotationWrappers(AA.class).size());
    }

    private void assertMethod1AnnotationBB(Method method) {
        assertFalse(method.isAnnotated(B.class)); // not @Repeatable
        assertTrue(method.isAnnotated(BB.class));
        assertEquals(0, method.getAnnotations(B.class).size());
        assertEquals(1, method.getAnnotations(BB.class).size());
        assertEquals(2, method.getAnnotation(BB.class).value().length);
        assertEquals(1, method.getAnnotationWrappers(BB.class).size());
        List<AnnotationWrapper> bb = method.getAnnotationWrapper(BB.class).getAnnotationsValue();
        assertEquals("b0", bb.get(0).getStringValue());
        assertEquals("b1", bb.get(1).getStringValue());
    }

    private void assertMethod1Parameters(Method method, List<Parameter> parameters) {
        assertEquals(3, parameters.size());
        assertMethod1Parameter0(method, parameters.get(0));
        assertMethod1Parameter1(method, parameters.get(1));
        assertMethod1Parameter2(method, parameters.get(2));
    }

    private void assertMethod1Parameter0(Method method, Parameter parameter) {
        assertEquals(method, parameter.getMethod());
        assertEquals("string", parameter.getName());
        assertEquals(Type.of(String.class), parameter.getType());

        assertFalse(parameter.isAnnotated(A.class));
        assertEquals(0, parameter.getAnnotations(A.class).size());
        assertEquals(0, parameter.getAnnotationWrappers().size());
    }

    private void assertMethod1Parameter1(Method method, Parameter parameter1) {
        assertEquals(method, parameter1.getMethod());
        assertEquals("bool", parameter1.getName());
        assertEquals(Type.of(boolean.class), parameter1.getType());

        assertTrue(parameter1.isAnnotated(A.class));
        assertEquals(1, parameter1.getAnnotations(A.class).size());
        assertEquals("ppp", parameter1.getAnnotations(A.class).get(0).value());
        assertEquals(1, parameter1.getAnnotationWrappers().size());

        AnnotationWrapper p1a0 = parameter1.getAnnotationWrappers().get(0);
        assertEquals("A", p1a0.getAnnotationType().getSimpleName());
        assertEquals(A.class.getName(), p1a0.getAnnotationType().getFullName());
        assertEquals(1, p1a0.getElementValues().size());
        assertEquals("ppp", p1a0.getElementValues().get("value"));
    }

    private void assertMethod1Parameter2(Method method, Parameter parameter) {
        assertEquals(method, parameter.getMethod());
        assertEquals("strings", parameter.getName());

        assertEquals("List", parameter.getType().getSimpleName());
        assertEquals("java.util.List<java.lang.String>", parameter.getType().getFullName());
        assertTrue(parameter.getType().isA(List.class));
        assertTrue(parameter.getType().isA(Collection.class));
        assertEquals(1, parameter.getType().getTypeParameters().size());
        assertEquals("String", parameter.getType().getTypeParameters().get(0).getSimpleName());
        assertEquals("java.lang.String", parameter.getType().getTypeParameters().get(0).getFullName());
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
                assertThat(field.getName()).matches("string|bool|map");
                count.getAndIncrement();
            }
        });

        assertEquals(5, count.get());
    }
}
