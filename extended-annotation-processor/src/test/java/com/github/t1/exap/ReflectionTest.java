package com.github.t1.exap;

import static com.github.t1.exap.ReflectionTest.FooNum.*;
import static java.lang.annotation.RetentionPolicy.*;
import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.assertThat;
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

    public enum FooNum {
        X,
        Y,
        Z;
    }

    @Retention(RUNTIME)
    public @interface Multi {
        boolean booly();

        byte bytey();

        short shorty();

        int inty();

        long longy();

        float floaty();

        double doubly();

        char chary();

        String stringy();

        Class<?> classy();

        A annoty();

        FooNum enumy();
    }

    @Retention(RUNTIME)
    public @interface MultiAR {
        MultiA[] value();
    }

    @Repeatable(MultiAR.class)
    @Retention(RUNTIME)
    public @interface MultiA {
        boolean[] booly();

        byte[] bytey();

        short[] shorty();

        int[] inty();

        long[] longy();

        float[] floaty();

        double[] doubly();

        char[] chary();

        String[] stringy();

        Class<?>[] classy();

        A[] annoty();

        FooNum[] enumy();
    }


    @Retention(RUNTIME)
    public @interface FooNumA {
        FooNum value();
    }

    @Retention(RUNTIME)
    public @interface FooNums {
        FooNum[] value();
    }

    @A("ttt")
    @JavaDoc(summary = "s", value = "v")
    public static class Pojo {
        boolean bool;

        @A("fff")
        String string;

        @FooNumA(X)
        @FooNums({ Y, Z })
        Map<String, Number> map;

        @Multi(booly = false, bytey = 1, chary = 0x21, classy = Object.class, doubly = 3.4D, floaty = 5.6F, inty = 7,
                longy = 8, shorty = 9, stringy = "s", annoty = @A("a") , enumy = X)
        @MultiA(booly = false, bytey = 1, chary = 0x21, classy = Object.class, doubly = 3.4D, floaty = 5.6F, inty = 7,
                longy = 8, shorty = 9, stringy = "s", annoty = @A("a") , enumy = X)
        @MultiA(booly = { false, true }, bytey = { 1, 2 }, chary = { 0x21, 0x22 },
                classy = { Object.class, String.class }, doubly = { 3.4D, 4.3D }, floaty = { 5.6F, 6.5F },
                inty = { 7, 8 }, longy = { 8, 9 }, shorty = { 9, 8 }, stringy = { "s", "t" },
                annoty = { @A("a"), @A("b") }, enumy = { X, Y })
        transient FooNum fooNum;

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
        assertEquals(1, a0.getValueMap().size());
        assertEquals("ttt", a0.getValueMap().get("value"));

        AnnotationWrapper a1 = wrappers.get(1);
        assertEquals("JavaDoc", a1.getAnnotationType().getSimpleName());
        assertEquals(JavaDoc.class.getName(), a1.getAnnotationType().getFullName());
        assertEquals(2, a1.getValueMap().size());
        assertEquals("s", a1.getValueMap().get("summary"));
        assertEquals("v", a1.getValueMap().get("value"));
    }

    @Test
    public void shouldGetFields() {
        List<Field> fields = type.getFields();

        assertEquals("fields size", 4, fields.size());
        assertBoolField(fields.get(0));
        assertStringField(fields.get(1));
        assertMapField(fields.get(2));
        assertEnumField(fields.get(3));
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
        assertEquals(1, a0.getValueMap().size());
        assertEquals("fff", a0.getValueMap().get("value"));
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

        assertMapFieldAnnotations(mapField);
    }

    private void assertMapFieldAnnotations(Field mapField) {
        assertEquals(2, mapField.getAnnotationWrappers().size());

        assertFalse(mapField.isAnnotated(A.class));
        assertEquals(0, mapField.getAnnotations(A.class).size());
        assertEquals(0, mapField.getAnnotationWrappers(A.class).size());

        assertTrue(mapField.isAnnotated(FooNumA.class));
        assertEquals(X, mapField.getAnnotation(FooNumA.class).value());
        assertEquals(asList(X), mapField.getAnnotationWrapper(FooNumA.class).getEnumValues());
        assertEquals(1, mapField.getAnnotations(FooNumA.class).size());
        assertEquals(1, mapField.getAnnotationWrappers(FooNumA.class).size());

        assertTrue(mapField.isAnnotated(FooNums.class));
        assertArrayEquals(new FooNum[] { Y, Z }, mapField.getAnnotation(FooNums.class).value());
        assertEquals(asList(Y, Z), mapField.getAnnotationWrapper(FooNums.class).getEnumValues());
        assertEquals(1, mapField.getAnnotations(FooNums.class).size());
        assertEquals(1, mapField.getAnnotationWrappers(FooNums.class).size());
    }

    private void assertEnumField(Field enumField) {
        assertEquals("fooNum", enumField.getName());
        assertEquals("FooNum", enumField.getType().getSimpleName());
        assertEquals("com.github.t1.exap.ReflectionTest$FooNum", enumField.getType().getFullName());
        assertFalse(enumField.isPublic());
        assertFalse(enumField.isStatic());
        assertTrue(enumField.isTransient());

        assertTrue(enumField.getType().isEnum());
        assertEquals(asList("X", "Y", "Z"), enumField.getType().getEnumValues());

        assertEquals(3, enumField.getAnnotationWrappers().size());
        assertEnumFieldAnnotationMulti(enumField);
        assertEnumFieldAnnotationMultiA1(enumField);
        assertEnumFieldAnnotationMultiA2(enumField);
    }

    private void assertEnumFieldAnnotationMulti(Field enumField) {
        assertTrue(enumField.isAnnotated(Multi.class));
        Multi multi = enumField.getAnnotation(Multi.class);
        assertEquals(false, multi.booly());
        assertEquals(1, multi.bytey());
        assertEquals(0x21, multi.chary());
        assertEquals(Object.class, multi.classy());
        assertEquals(3.4d, multi.doubly(), 0.01d);
        assertEquals(5.6f, multi.floaty(), 0.01f);
        assertEquals(7, multi.inty());
        assertEquals(8, multi.longy());
        assertEquals(9, multi.shorty());
        assertEquals("s", multi.stringy());
        assertEquals("a", multi.annoty().value());
        assertEquals(X, multi.enumy());

        AnnotationWrapper wrapper = enumField.getAnnotationWrapper(Multi.class);
        assertEquals(false, wrapper.getBooleanValue("booly"));
        assertEquals(asList(false), wrapper.getBooleanValues("booly"));
        assertEquals(1, wrapper.getByteValue("bytey"));
        assertEquals(asList((byte) 1), wrapper.getByteValues("bytey"));
        assertEquals(0x21, wrapper.getCharValue("chary"));
        assertEquals(asList((char) 0x21), wrapper.getCharValues("chary"));
        assertEquals(Type.of(Object.class), wrapper.getTypeValue("classy"));
        assertEquals(asList(Type.of(Object.class)), wrapper.getTypeValues("classy"));
        assertEquals(3.4d, wrapper.getDoubleValue("doubly"), 0.01d);
        assertEquals(asList(3.4d), wrapper.getDoubleValues("doubly"));
        assertEquals(5.6f, wrapper.getFloatValue("floaty"), 0.01f);
        assertEquals(asList(5.6f), wrapper.getFloatValues("floaty"));
        assertEquals(7, wrapper.getIntValue("inty"));
        assertEquals(asList(7), wrapper.getIntValues("inty"));
        assertEquals(8, wrapper.getLongValue("longy"));
        assertEquals(asList((long) 8), wrapper.getLongValues("longy"));
        assertEquals(9, wrapper.getShortValue("shorty"));
        assertEquals(asList((short) 9), wrapper.getShortValues("shorty"));
        assertEquals("s", wrapper.getStringValue("stringy"));
        assertEquals(asList("s"), wrapper.getStringValues("stringy"));
        assertEquals("a", wrapper.getAnnotationValue("annoty").getStringValue());
        List<AnnotationWrapper> annotys = wrapper.getAnnotationValues("annoty");
        assertEquals(1, annotys.size());
        assertEquals("a", annotys.get(0).getStringValue());
        assertEquals(X, wrapper.getEnumValue("enumy"));
        assertEquals(asList(X), wrapper.getEnumValues("enumy"));

        Map<String, Object> valueMap = wrapper.getValueMap();
        assertEquals(false, valueMap.get("booly"));
        assertEquals((byte) 1, valueMap.get("bytey"));
        assertEquals((char) 0x21, valueMap.get("chary"));
        assertEquals(Type.of(Object.class), valueMap.get("classy"));
        assertEquals(3.4d, valueMap.get("doubly"));
        assertEquals(5.6f, valueMap.get("floaty"));
        assertEquals(7, valueMap.get("inty"));
        assertEquals(8L, valueMap.get("longy"));
        assertEquals((short) 9, valueMap.get("shorty"));
        assertEquals("s", valueMap.get("stringy"));
        // TODO assertEquals("s", valueMap.get("annoty"));
        assertEquals(X, valueMap.get("enumy"));

        assertThat(wrapper.getValueNames()).containsOnly("booly", "bytey", "chary", "classy", "doubly", "floaty",
                "inty", "longy", "shorty", "stringy", "annoty", "enumy");
    }

    private void assertEnumFieldAnnotationMultiA1(Field enumField) {
        assertTrue(enumField.isAnnotated(MultiA.class));
        MultiA multia = enumField.getAnnotations(MultiA.class).get(0);
        assertArrayEquals(new boolean[] { false }, multia.booly());
        assertArrayEquals(new byte[] { 1 }, multia.bytey());
        assertArrayEquals(new char[] { 0x21 }, multia.chary());
        assertArrayEquals(new Class[] { Object.class }, multia.classy());
        assertArrayEquals(new double[] { 3.4d }, multia.doubly(), 0.01d);
        assertArrayEquals(new float[] { 5.6f }, multia.floaty(), 0.01f);
        assertArrayEquals(new int[] { 7 }, multia.inty());
        assertArrayEquals(new long[] { 8 }, multia.longy());
        assertArrayEquals(new short[] { 9 }, multia.shorty());
        assertArrayEquals(new String[] { "s" }, multia.stringy());
        // TODO assertArrayEquals("a", multia.annoty().value());
        assertArrayEquals(new FooNum[] { X }, multia.enumy());

        AnnotationWrapper wrapper = enumField.getAnnotationWrappers(MultiA.class).get(0);

        assertThat(wrapper.getValueNames()).containsOnly("booly", "bytey", "chary", "classy", "doubly", "floaty",
                "inty", "longy", "shorty", "stringy", "annoty", "enumy");

        assertEquals(false, wrapper.getBooleanValue("booly"));
        assertEquals((byte) 1, wrapper.getByteValue("bytey"));
        assertEquals((char) 0x21, wrapper.getCharValue("chary"));
        assertEquals(Type.of(Object.class), wrapper.getTypeValue("classy"));
        assertEquals(3.4d, wrapper.getDoubleValue("doubly"), 0.01d);
        assertEquals(5.6f, wrapper.getFloatValue("floaty"), 0.01f);
        assertEquals(7, wrapper.getIntValue("inty"));
        assertEquals(8L, wrapper.getLongValue("longy"));
        assertEquals((short) 9, wrapper.getShortValue("shorty"));
        assertEquals("s", wrapper.getStringValue("stringy"));
        // TODO assertEquals("s", wrapper.getAnnotationsValue("annoty"));
        assertEquals(X, wrapper.getEnumValue("enumy"));

        assertEquals(asList(false), wrapper.getBooleanValues("booly"));
        assertEquals(asList((byte) 1), wrapper.getByteValues("bytey"));
        assertEquals(asList((char) 0x21), wrapper.getCharValues("chary"));
        assertEquals(asList(Type.of(Object.class)), wrapper.getTypeValues("classy"));
        assertEquals(asList(3.4d), wrapper.getDoubleValues("doubly"));
        assertEquals(asList(5.6f), wrapper.getFloatValues("floaty"));
        assertEquals(asList(7), wrapper.getIntValues("inty"));
        assertEquals(asList(8L), wrapper.getLongValues("longy"));
        assertEquals(asList((short) 9), wrapper.getShortValues("shorty"));
        assertEquals(asList("s"), wrapper.getStringValues("stringy"));
        // TODO assertEquals("s", wrapper.getAnnotationsValues("annoty"));
        assertEquals(asList(X), wrapper.getEnumValues("enumy"));

        Map<String, Object> valueMap = wrapper.getValueMap();
        assertEquals(asList(false), valueMap.get("booly"));
        assertEquals(asList((byte) 1), valueMap.get("bytey"));
        assertEquals(asList((char) 0x21), valueMap.get("chary"));
        assertEquals(asList(Type.of(Object.class)), valueMap.get("classy"));
        assertEquals(asList(3.4d), valueMap.get("doubly"));
        assertEquals(asList(5.6f), valueMap.get("floaty"));
        assertEquals(asList(7), valueMap.get("inty"));
        assertEquals(asList(8L), valueMap.get("longy"));
        assertEquals(asList((short) 9), valueMap.get("shorty"));
        assertEquals(asList("s"), valueMap.get("stringy"));
        // TODO assertEquals(asList("s"), valueMap.get("annoty"));
        assertEquals(asList(X), valueMap.get("enumy"));
    }

    private void assertEnumFieldAnnotationMultiA2(Field enumField) {
        assertTrue(enumField.isAnnotated(MultiA.class));
        MultiA multia = enumField.getAnnotations(MultiA.class).get(1);
        assertArrayEquals(new boolean[] { false, true }, multia.booly());
        assertArrayEquals(new byte[] { 1, 2 }, multia.bytey());
        assertArrayEquals(new char[] { 0x21, 0x22 }, multia.chary());
        assertArrayEquals(new Class[] { Object.class, String.class }, multia.classy());
        assertArrayEquals(new double[] { 3.4d, 4.3d }, multia.doubly(), 0.01d);
        assertArrayEquals(new float[] { 5.6f, 6.5f }, multia.floaty(), 0.01f);
        assertArrayEquals(new int[] { 7, 8 }, multia.inty());
        assertArrayEquals(new long[] { 8, 9 }, multia.longy());
        assertArrayEquals(new short[] { 9, 8 }, multia.shorty());
        assertArrayEquals(new String[] { "s", "t" }, multia.stringy());
        // TODO assertArrayEquals("a", multia.annoty().value());
        assertArrayEquals(new FooNum[] { X, Y }, multia.enumy());

        AnnotationWrapper wrapper = enumField.getAnnotationWrappers(MultiA.class).get(1);
        assertEquals(asList(false, true), wrapper.getBooleanValues("booly"));
        assertEquals(asList((byte) 1, (byte) 2), wrapper.getByteValues("bytey"));
        assertEquals(asList((char) 0x21, (char) 0x22), wrapper.getCharValues("chary"));
        assertEquals(asList(Type.of(Object.class), Type.of(String.class)), wrapper.getTypeValues("classy"));
        assertEquals(asList(3.4d, 4.3d), wrapper.getDoubleValues("doubly"));
        assertEquals(asList(5.6f, 6.5f), wrapper.getFloatValues("floaty"));
        assertEquals(asList(7, 8), wrapper.getIntValues("inty"));
        assertEquals(asList(8L, 9L), wrapper.getLongValues("longy"));
        assertEquals(asList((short) 9, (short) 8), wrapper.getShortValues("shorty"));
        assertEquals(asList("s", "t"), wrapper.getStringValues("stringy"));
        // TODO assertEquals(asList("s"), wrapper.getAnnotationsValues("annoty"));
        assertEquals(asList(X, Y), wrapper.getEnumValues("enumy"));

        Map<String, Object> valueMap = wrapper.getValueMap();
        assertEquals(asList(false, true), valueMap.get("booly"));
        assertEquals(asList((byte) 1, (byte) 2), valueMap.get("bytey"));
        assertEquals(asList((char) 0x21, (char) 0x22), valueMap.get("chary"));
        assertEquals(asList(Type.of(Object.class), Type.of(String.class)), valueMap.get("classy"));
        assertEquals(asList(3.4d, 4.3d), valueMap.get("doubly"));
        assertEquals(asList(5.6f, 6.5f), valueMap.get("floaty"));
        assertEquals(asList(7, 8), valueMap.get("inty"));
        assertEquals(asList(8L, 9L), valueMap.get("longy"));
        assertEquals(asList((short) 9, (short) 8), valueMap.get("shorty"));
        assertEquals(asList("s", "t"), valueMap.get("stringy"));
        // TODO assertEquals(asList("s"), valueMap.get("annoty"));
        assertEquals(asList(X, Y), valueMap.get("enumy"));

        assertThat(wrapper.getValueNames()).containsOnly("booly", "bytey", "chary", "classy", "doubly", "floaty",
                "inty", "longy", "shorty", "stringy", "annoty", "enumy");
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
        assertTrue(method.isAnnotated(AA.class)); // not @Repeatable
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

        assertFalse(method.isAnnotated(BB.class)); // @Repeatable

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
        assertEquals(1, annotation.getValueMap().size());
        assertEquals(value, annotation.getValueMap().get("value"));
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
        List<AnnotationWrapper> bb = method.getAnnotationWrapper(BB.class).getAnnotationValues();
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
        assertEquals(1, p1a0.getValueMap().size());
        assertEquals("ppp", p1a0.getValueMap().get("value"));
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
                assertThat(field.getName()).matches("string|bool|map|fooNum");
                count.getAndIncrement();
            }
        });

        assertEquals(6, count.get());
    }
}
