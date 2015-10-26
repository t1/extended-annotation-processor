package com.github.t1.exap.reflection;

import static com.github.t1.exap.reflection.AnnotationPropertyType.*;
import static com.github.t1.exap.reflection.ReflectionTest.FooNum.*;
import static java.lang.annotation.RetentionPolicy.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.*;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.Assert.*;

import java.lang.annotation.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;

import com.github.t1.exap.JavaDoc;
import com.github.t1.exap.reflection.ReflectionTest.Container.Nested;

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
        Z {
            @Override
            public String toString() {
                return "Z!";
            }
        };
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

        FooNum enumy();

        A annoty();
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

        FooNum[] enumy();

        A[] annoty();
    }


    @Retention(RUNTIME)
    public @interface FooNumA {
        FooNum value();
    }

    @Retention(RUNTIME)
    public @interface FooNums {
        FooNum[] value();
    }

    public static class Container {
        public static class Inner extends Container {}

        public static class Nested extends Inner {}
    }

    @A("ttt")
    @JavaDoc(value = "s. v")
    public static class Pojo {
        public static final String CONSTANT = "dummy";

        public static String constant() {
            return CONSTANT;
        }

        boolean bool;

        @A("fff")
        @JavaDoc(value = "s")
        String string;

        @FooNumA(X)
        @FooNums({ Y, Z })
        Map<String, Number> map;

        @Multi(booly = false, bytey = 1, chary = 0x21, classy = Object.class, doubly = 3.4D, floaty = 5.6F, inty = 7,
                longy = 8, shorty = 9, stringy = "s", enumy = X, annoty = @A("a") )
        @MultiA(booly = {}, bytey = {}, chary = {}, classy = {}, doubly = {}, floaty = {}, inty = {}, longy = {},
                shorty = {}, stringy = {}, enumy = {}, annoty = {})
        @MultiA(booly = false, bytey = 1, chary = 0x21, classy = Object.class, doubly = 3.4D, floaty = 5.6F, inty = 7,
                longy = 8, shorty = 9, stringy = "s", enumy = X, annoty = @A("a") )
        @MultiA(booly = { false, true }, bytey = { 1, 2 }, chary = { 0x21, 0x22 },
                classy = { Object.class, String.class }, doubly = { 3.4D, 4.3D }, floaty = { 5.6F, 6.5F },
                inty = { 7, 8 }, longy = { 8, 9 }, shorty = { 9, 8 }, stringy = { "s", "t" }, enumy = { Y, Z },
                annoty = { @A("a"), @A("b") })
        transient FooNum fooNum;

        @A("mmm")
        @A("nnn")
        @B("bbb")
        public void method0() {}

        @A("ooo")
        @BB({ @B("b0"), @B("b1") })
        @SuppressWarnings("unused")
        public List<String> method1(String string, @A("ppp") boolean bool, List<String> strings) {
            return null;
        }

        public Nested method2() {
            return null;
        }
    }

    private final Type type = ReflectionType.type(Pojo.class);

    @Test
    public void assertType() {
        assertEquals("Pojo", type.getSimpleName());
        assertEquals(Pojo.class.getName(), type.getFullName());
        assertFalse(type.isVoid());
        assertFalse(type.isPrimitive());
        assertFalse(type.isBoolean());
        assertFalse(type.isNumber());
        assertFalse(type.isInteger());
        assertFalse(type.isFloating());
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

    @Test
    @SuppressWarnings("unused")
    public void assertParameterizedType() {
        @A("a")
        class Wrapper<T> {
            String field;

            void method() {}
        }
        class Gen {
            Wrapper<String> c;
        }

        Type type = ReflectionType.type(Gen.class).getField("c").getType();

        assertEquals("Wrapper", type.getSimpleName());
        assertEquals(Wrapper.class.getName() + "<java.lang.String>", type.getFullName());
        assertFalse(type.isVoid());
        assertFalse(type.isPrimitive());
        assertFalse(type.isBoolean());
        assertFalse(type.isNumber());
        assertFalse(type.isInteger());
        assertFalse(type.isFloating());
        assertFalse(type.isString());
        assertFalse(type.isEnum());
        assertNull(type.getEnumValues());
        assertFalse(type.isArray());
        assertNull(type.elementType());
        assertFalse(type.isA(String.class));

        assertEquals(1, type.getTypeParameters().size());
        assertEquals(ReflectionType.type(String.class), type.getTypeParameters().get(0));

        assertFalse(type.isPublic());
        assertFalse(type.isStatic());
        assertFalse(type.isTransient()); // doesn't make sense, but must not lie

        assertTrue(type.isAnnotated(A.class));
        assertEquals(1, type.getAnnotations(A.class).size());
        assertEquals("a", type.getAnnotations(A.class).get(0).value());
        assertEquals(1, type.getAnnotationWrappers(A.class).size());
        assertEquals("a", type.getAnnotationWrappers(A.class).get(0).getProperty("value"));

        assertEquals("fields: " + type.getFields(), 2, type.getFields().size());
        assertThat(type.getFields()).extracting(f -> f.getName()).containsOnly("field", "this$0");

        assertEquals(1, type.getMethods().size());
        Method method = type.getMethods().get(0);
        assertEquals("method", method.getName());
        assertTrue(method.getReturnType().isVoid());
    }

    @Test
    public void misc() {
        assertTrue(ReflectionType.type(Void.class).isVoid());

        assertTrue(ReflectionType.type(Boolean.class).isBoolean());
        assertTrue(ReflectionType.type(boolean.class).isBoolean());

        assertTrue(ReflectionType.type(Character.class).isCharacter());
        assertTrue(ReflectionType.type(char.class).isCharacter());
        assertFalse(ReflectionType.type(boolean.class).isCharacter());

        assertTrue(ReflectionType.type(Byte.class).isInteger());
        assertTrue(ReflectionType.type(byte.class).isInteger());
        assertTrue(ReflectionType.type(Short.class).isInteger());
        assertTrue(ReflectionType.type(short.class).isInteger());
        assertTrue(ReflectionType.type(Integer.class).isInteger());
        assertTrue(ReflectionType.type(int.class).isInteger());
        assertTrue(ReflectionType.type(Long.class).isInteger());
        assertTrue(ReflectionType.type(long.class).isInteger());

        assertTrue(ReflectionType.type(Float.class).isFloating());
        assertTrue(ReflectionType.type(float.class).isFloating());
        assertTrue(ReflectionType.type(Double.class).isFloating());
        assertTrue(ReflectionType.type(double.class).isFloating());

        assertEquals(ReflectionType.type(String.class), ReflectionType.type(String[].class).elementType());

        assertEquals(ReflectionType.type(Number.class), ReflectionType.type(Integer.class).getSuperType());

        assertEquals("ReflectionType:java.lang.Integer", ReflectionType.type(Integer.class).toString());
        assertNotNull(ReflectionType.type(Integer.class).hashCode());

        assertTrue(ReflectionType.type(String.class).equals(ReflectionType.type(String.class)));
        assertFalse(ReflectionType.type(String.class).equals(ReflectionType.type(Long.class)));
        assertFalse(ReflectionType.type(String.class).equals(null));
        assertFalse(ReflectionType.type(String.class).equals(String.class));
    }

    // TODO extract JavaDoc tags
    // TODO convert JavaDoc-HTML to Markdown

    @Test
    public void assertTypeAnnotations() {
        assertTrue(type.isAnnotated(A.class));
        assertEquals(1, type.getAnnotations(A.class).size());
        assertEquals("ttt", type.getAnnotations(A.class).get(0).value());
        assertEquals(1, type.getAnnotationWrappers(A.class).size());
        assertEquals("ttt", type.getAnnotationWrappers(A.class).get(0).getProperty("value"));

        assertTrue(type.isAnnotated(JavaDoc.class));
        assertEquals("s", JavaDoc.SUMMARY.apply(type.getAnnotation(JavaDoc.class)));
        assertEquals("s. v", type.getAnnotation(JavaDoc.class).value());

        List<AnnotationWrapper> wrappers = type.getAnnotationWrappers();
        assertEquals(2, wrappers.size());

        AnnotationWrapper a0 = wrappers.get(0);
        assertEquals("A", a0.getAnnotationType().getSimpleName());
        assertEquals(A.class.getName(), a0.getAnnotationType().getFullName());
        assertEquals(1, a0.getPropertyMap().size());
        assertEquals("ttt", a0.getPropertyMap().get("value"));

        AnnotationWrapper a1 = wrappers.get(1);
        assertEquals("JavaDoc", a1.getAnnotationType().getSimpleName());
        assertEquals(JavaDoc.class.getName(), a1.getAnnotationType().getFullName());
        assertEquals(1, a1.getPropertyMap().size());
        assertEquals("s. v", a1.getPropertyMap().get("value"));
    }

    @Test
    public void shouldGetFields() {
        List<Field> fields = type.getFields();

        assertEquals("fields: " + fields, 4, fields.size());
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
        assertEquals("fff", stringField.getAnnotationWrappers(A.class).get(0).getProperty("value"));

        assertEquals(2, stringField.getAnnotationWrappers().size());

        AnnotationWrapper a0 = stringField.getAnnotationWrappers().get(0);
        assertEquals("A", a0.getAnnotationType().getSimpleName());
        assertEquals(A.class.getName(), a0.getAnnotationType().getFullName());
        assertEquals(1, a0.getPropertyMap().size());
        assertEquals("fff", a0.getPropertyMap().get("value"));

        AnnotationWrapper a1 = stringField.getAnnotationWrappers().get(1);
        assertEquals("JavaDoc", a1.getAnnotationType().getSimpleName());
        assertEquals(JavaDoc.class.getName(), a1.getAnnotationType().getFullName());
        assertEquals(1, a1.getPropertyMap().size());
        assertEquals("s", a1.getPropertyMap().get("value"));
        assertEquals("s", JavaDoc.SUMMARY.apply(stringField.getAnnotation(JavaDoc.class)));
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
        assertEquals(asList("X"), mapField.getAnnotationWrapper(FooNumA.class).getEnumProperties("value"));
        assertEquals(1, mapField.getAnnotations(FooNumA.class).size());
        assertEquals(1, mapField.getAnnotationWrappers(FooNumA.class).size());

        assertTrue(mapField.isAnnotated(FooNums.class));
        assertArrayEquals(new FooNum[] { Y, Z }, mapField.getAnnotation(FooNums.class).value());
        assertEquals(asList("Y", "Z"), mapField.getAnnotationWrapper(FooNums.class).getEnumProperties("value"));
        assertEquals(1, mapField.getAnnotations(FooNums.class).size());
        assertEquals(1, mapField.getAnnotationWrappers(FooNums.class).size());
    }

    private void assertEnumField(Field enumField) {
        assertEquals("fooNum", enumField.getName());
        assertEquals("FooNum", enumField.getType().getSimpleName());
        assertEquals(FooNum.class.getName(), enumField.getType().getFullName());
        assertFalse(enumField.isPublic());
        assertFalse(enumField.isStatic());
        assertTrue(enumField.isTransient());

        assertTrue(enumField.getType().isEnum());
        assertEquals(asList("X", "Y", "Z"), enumField.getType().getEnumValues());

        assertEquals(4, enumField.getAnnotationWrappers().size());
        assertEnumFieldAnnotationMulti(enumField);
        assertEnumFieldAnnotationMultiA0(enumField);
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
        assertEquals(X, multi.enumy());
        assertEquals("a", multi.annoty().value());

        AnnotationWrapper wrapper = enumField.getAnnotationWrapper(Multi.class);

        assertPropertyNamesMulti(wrapper);
        assertPropertyTypesMulti(wrapper);
        assertIsPropertyArrayMulti(false, wrapper);

        assertEquals(false, wrapper.getBooleanProperty("booly"));
        assertEquals(asList(false), wrapper.getBooleanProperties("booly"));
        assertEquals(1, wrapper.getByteProperty("bytey"));
        assertEquals(asList((byte) 1), wrapper.getByteProperties("bytey"));
        assertEquals(0x21, wrapper.getCharProperty("chary"));
        assertEquals(asList((char) 0x21), wrapper.getCharProperties("chary"));
        assertEquals(ReflectionType.type(Object.class), wrapper.getTypeProperty("classy"));
        assertEquals(asList(ReflectionType.type(Object.class)), wrapper.getTypeProperties("classy"));
        assertEquals(3.4d, wrapper.getDoubleProperty("doubly"), 0.01d);
        assertEquals(asList(3.4d), wrapper.getDoubleProperties("doubly"));
        assertEquals(5.6f, wrapper.getFloatProperty("floaty"), 0.01f);
        assertEquals(asList(5.6f), wrapper.getFloatProperties("floaty"));
        assertEquals(7, wrapper.getIntProperty("inty"));
        assertEquals(asList(7), wrapper.getIntProperties("inty"));
        assertEquals(8, wrapper.getLongProperty("longy"));
        assertEquals(asList((long) 8), wrapper.getLongProperties("longy"));
        assertEquals(9, wrapper.getShortProperty("shorty"));
        assertEquals(asList((short) 9), wrapper.getShortProperties("shorty"));
        assertEquals("s", wrapper.getStringProperty("stringy"));
        assertEquals(asList("s"), wrapper.getStringProperties("stringy"));
        assertEquals("X", wrapper.getEnumProperty("enumy"));
        assertEquals(asList("X"), wrapper.getEnumProperties("enumy"));
        assertEquals("a", wrapper.getAnnotationProperty("annoty").getStringProperty("value"));
        List<AnnotationWrapper> annotys = wrapper.getAnnotationProperties("annoty");
        assertEquals(1, annotys.size());
        assertEquals("a", annotys.get(0).getStringProperty("value"));

        Map<String, Object> valueMap = wrapper.getPropertyMap();
        assertEquals(false, valueMap.get("booly"));
        assertEquals((byte) 1, valueMap.get("bytey"));
        assertEquals((char) 0x21, valueMap.get("chary"));
        assertEquals(ReflectionType.type(Object.class), valueMap.get("classy"));
        assertEquals(3.4d, valueMap.get("doubly"));
        assertEquals(5.6f, valueMap.get("floaty"));
        assertEquals(7, valueMap.get("inty"));
        assertEquals(8L, valueMap.get("longy"));
        assertEquals((short) 9, valueMap.get("shorty"));
        assertEquals("s", valueMap.get("stringy"));
        assertEquals(X, valueMap.get("enumy"));
        assertEquals(multi.annoty(), valueMap.get("annoty"));
    }

    private void assertEnumFieldAnnotationMultiA0(Field enumField) {
        assertTrue(enumField.isAnnotated(MultiA.class));
        MultiA multia = enumField.getAnnotations(MultiA.class).get(0);
        assertArrayEquals(new boolean[] {}, multia.booly());
        assertArrayEquals(new byte[] {}, multia.bytey());
        assertArrayEquals(new char[] {}, multia.chary());
        assertArrayEquals(new Class[] {}, multia.classy());
        assertArrayEquals(new double[] {}, multia.doubly(), 0.01d);
        assertArrayEquals(new float[] {}, multia.floaty(), 0.01f);
        assertArrayEquals(new int[] {}, multia.inty());
        assertArrayEquals(new long[] {}, multia.longy());
        assertArrayEquals(new short[] {}, multia.shorty());
        assertArrayEquals(new String[] {}, multia.stringy());
        assertArrayEquals(new FooNum[] {}, multia.enumy());
        assertArrayEquals(new Annotation[] {}, multia.annoty());

        AnnotationWrapper wrapper = enumField.getAnnotationWrappers(MultiA.class).get(0);

        assertPropertyNamesMulti(wrapper);
        assertPropertyTypesMulti(wrapper);
        assertIsPropertyArrayMulti(true, wrapper);

        assertThrownUnexpectedArraySizes(wrapper, 0);

        assertEquals(emptyList(), wrapper.getBooleanProperties("booly"));
        assertEquals(emptyList(), wrapper.getByteProperties("bytey"));
        assertEquals(emptyList(), wrapper.getCharProperties("chary"));
        assertEquals(emptyList(), wrapper.getTypeProperties("classy"));
        assertEquals(emptyList(), wrapper.getDoubleProperties("doubly"));
        assertEquals(emptyList(), wrapper.getFloatProperties("floaty"));
        assertEquals(emptyList(), wrapper.getIntProperties("inty"));
        assertEquals(emptyList(), wrapper.getLongProperties("longy"));
        assertEquals(emptyList(), wrapper.getShortProperties("shorty"));
        assertEquals(emptyList(), wrapper.getStringProperties("stringy"));
        assertEquals(emptyList(), wrapper.getEnumProperties("enumy"));
        assertEquals(emptyList(), wrapper.getAnnotationProperties("annoty"));

        Map<String, Object> valueMap = wrapper.getPropertyMap();
        assertEquals(emptyList(), valueMap.get("booly"));
        assertEquals(emptyList(), valueMap.get("bytey"));
        assertEquals(emptyList(), valueMap.get("chary"));
        assertEquals(emptyList(), valueMap.get("classy"));
        assertEquals(emptyList(), valueMap.get("doubly"));
        assertEquals(emptyList(), valueMap.get("floaty"));
        assertEquals(emptyList(), valueMap.get("inty"));
        assertEquals(emptyList(), valueMap.get("longy"));
        assertEquals(emptyList(), valueMap.get("shorty"));
        assertEquals(emptyList(), valueMap.get("stringy"));
        assertEquals(emptyList(), valueMap.get("enumy"));
        assertEquals(emptyList(), valueMap.get("annoty"));
    }

    private void assertEnumFieldAnnotationMultiA1(Field enumField) {
        assertTrue(enumField.isAnnotated(MultiA.class));
        MultiA multia = enumField.getAnnotations(MultiA.class).get(1);
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
        assertArrayEquals(new FooNum[] { X }, multia.enumy());
        assertEquals(1, multia.annoty().length);
        assertEquals("a", multia.annoty()[0].value());

        AnnotationWrapper wrapper = enumField.getAnnotationWrappers(MultiA.class).get(1);

        assertPropertyNamesMulti(wrapper);
        assertPropertyTypesMulti(wrapper);
        assertIsPropertyArrayMulti(true, wrapper);

        assertEquals(false, wrapper.getBooleanProperty("booly"));
        assertEquals((byte) 1, wrapper.getByteProperty("bytey"));
        assertEquals((char) 0x21, wrapper.getCharProperty("chary"));
        assertEquals(ReflectionType.type(Object.class), wrapper.getTypeProperty("classy"));
        assertEquals(3.4d, wrapper.getDoubleProperty("doubly"), 0.01d);
        assertEquals(5.6f, wrapper.getFloatProperty("floaty"), 0.01f);
        assertEquals(7, wrapper.getIntProperty("inty"));
        assertEquals(8L, wrapper.getLongProperty("longy"));
        assertEquals((short) 9, wrapper.getShortProperty("shorty"));
        assertEquals("s", wrapper.getStringProperty("stringy"));
        assertEquals("X", wrapper.getEnumProperty("enumy"));
        assertEquals("a", wrapper.getAnnotationProperty("annoty").getStringProperty("value"));

        assertEquals(asList(false), wrapper.getBooleanProperties("booly"));
        assertEquals(asList((byte) 1), wrapper.getByteProperties("bytey"));
        assertEquals(asList((char) 0x21), wrapper.getCharProperties("chary"));
        assertEquals(asList(ReflectionType.type(Object.class)), wrapper.getTypeProperties("classy"));
        assertEquals(asList(3.4d), wrapper.getDoubleProperties("doubly"));
        assertEquals(asList(5.6f), wrapper.getFloatProperties("floaty"));
        assertEquals(asList(7), wrapper.getIntProperties("inty"));
        assertEquals(asList(8L), wrapper.getLongProperties("longy"));
        assertEquals(asList((short) 9), wrapper.getShortProperties("shorty"));
        assertEquals(asList("s"), wrapper.getStringProperties("stringy"));
        assertEquals(asList("X"), wrapper.getEnumProperties("enumy"));
        assertEquals(1, wrapper.getAnnotationProperties("annoty").size());
        assertEquals("a", wrapper.getAnnotationProperties("annoty").get(0).getStringProperty("value"));

        Map<String, Object> valueMap = wrapper.getPropertyMap();
        assertEquals(asList(false), valueMap.get("booly"));
        assertEquals(asList((byte) 1), valueMap.get("bytey"));
        assertEquals(asList((char) 0x21), valueMap.get("chary"));
        assertEquals(asList(ReflectionType.type(Object.class)), valueMap.get("classy"));
        assertEquals(asList(3.4d), valueMap.get("doubly"));
        assertEquals(asList(5.6f), valueMap.get("floaty"));
        assertEquals(asList(7), valueMap.get("inty"));
        assertEquals(asList(8L), valueMap.get("longy"));
        assertEquals(asList((short) 9), valueMap.get("shorty"));
        assertEquals(asList("s"), valueMap.get("stringy"));
        assertEquals(asList(X), valueMap.get("enumy"));
        assertEquals(1, ((List<?>) valueMap.get("annoty")).size());
        assertEquals(multia.annoty()[0], ((List<?>) valueMap.get("annoty")).get(0));
    }

    private void assertEnumFieldAnnotationMultiA2(Field enumField) {
        assertTrue(enumField.isAnnotated(MultiA.class));
        MultiA multia = enumField.getAnnotations(MultiA.class).get(2);
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
        assertArrayEquals(new FooNum[] { Y, Z }, multia.enumy());
        assertEquals(2, multia.annoty().length);
        assertEquals("a", multia.annoty()[0].value());
        assertEquals("b", multia.annoty()[1].value());

        AnnotationWrapper wrapper = enumField.getAnnotationWrappers(MultiA.class).get(2);
        assertPropertyNamesMulti(wrapper);
        assertPropertyTypesMulti(wrapper);
        assertIsPropertyArrayMulti(true, wrapper);

        assertThrownUnexpectedArraySizes(wrapper, 2);

        assertEquals(asList(false, true), wrapper.getBooleanProperties("booly"));
        assertEquals(asList((byte) 1, (byte) 2), wrapper.getByteProperties("bytey"));
        assertEquals(asList((char) 0x21, (char) 0x22), wrapper.getCharProperties("chary"));
        assertEquals(asList(ReflectionType.type(Object.class), ReflectionType.type(String.class)),
                wrapper.getTypeProperties("classy"));
        assertEquals(asList(3.4d, 4.3d), wrapper.getDoubleProperties("doubly"));
        assertEquals(asList(5.6f, 6.5f), wrapper.getFloatProperties("floaty"));
        assertEquals(asList(7, 8), wrapper.getIntProperties("inty"));
        assertEquals(asList(8L, 9L), wrapper.getLongProperties("longy"));
        assertEquals(asList((short) 9, (short) 8), wrapper.getShortProperties("shorty"));
        assertEquals(asList("s", "t"), wrapper.getStringProperties("stringy"));
        assertEquals(asList("Y", "Z"), wrapper.getEnumProperties("enumy"));
        assertEquals(2, wrapper.getAnnotationProperties("annoty").size());
        assertEquals(multia.annoty()[0].value(),
                wrapper.getAnnotationProperties("annoty").get(0).getSingleProperty("value"));
        assertEquals(multia.annoty()[1].value(),
                wrapper.getAnnotationProperties("annoty").get(1).getStringProperty("value"));

        Map<String, Object> valueMap = wrapper.getPropertyMap();
        assertEquals(asList(false, true), valueMap.get("booly"));
        assertEquals(asList((byte) 1, (byte) 2), valueMap.get("bytey"));
        assertEquals(asList((char) 0x21, (char) 0x22), valueMap.get("chary"));
        assertEquals(asList(ReflectionType.type(Object.class), ReflectionType.type(String.class)),
                valueMap.get("classy"));
        assertEquals(asList(3.4d, 4.3d), valueMap.get("doubly"));
        assertEquals(asList(5.6f, 6.5f), valueMap.get("floaty"));
        assertEquals(asList(7, 8), valueMap.get("inty"));
        assertEquals(asList(8L, 9L), valueMap.get("longy"));
        assertEquals(asList((short) 9, (short) 8), valueMap.get("shorty"));
        assertEquals(asList("s", "t"), valueMap.get("stringy"));
        assertEquals(asList(Y, Z), valueMap.get("enumy"));
        assertEquals(2, ((List<?>) valueMap.get("annoty")).size());
        assertEquals(multia.annoty()[0], ((List<?>) valueMap.get("annoty")).get(0));
        assertEquals(multia.annoty()[1], ((List<?>) valueMap.get("annoty")).get(1));
    }

    private void assertThrownUnexpectedArraySizes(AnnotationWrapper wrapper, int expectedSizeMessage) {
        assertThrownUnexpectedArraySize(() -> wrapper.getBooleanProperty("booly"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getByteProperty("bytey"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getCharProperty("chary"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getTypeProperty("classy"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getDoubleProperty("doubly"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getFloatProperty("floaty"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getIntProperty("inty"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getLongProperty("longy"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getShortProperty("shorty"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getStringProperty("stringy"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getEnumProperty("enumy"), expectedSizeMessage);
        assertThrownUnexpectedArraySize(() -> wrapper.getAnnotationProperty("annoty"), expectedSizeMessage);
    }

    private void assertThrownUnexpectedArraySize(ThrowingCallable thrower, int size) {
        assertThatThrownBy(thrower)
                .hasMessage("expected annotation property array to contain exactly one element but found " + size);
    }

    private void assertPropertyNamesMulti(AnnotationWrapper wrapper) {
        assertThat(wrapper.getPropertyNames()).containsOnly("booly", "bytey", "chary", "classy", "doubly", "floaty",
                "inty", "longy", "shorty", "stringy", "enumy", "annoty");
    }

    private void assertIsPropertyArrayMulti(boolean expectedIsArray, AnnotationWrapper wrapper) {
        assertEquals(expectedIsArray, wrapper.isArrayProperty("booly"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("bytey"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("chary"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("classy"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("doubly"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("floaty"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("inty"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("longy"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("shorty"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("stringy"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("enumy"));
        assertEquals(expectedIsArray, wrapper.isArrayProperty("annoty"));
    }

    private void assertPropertyTypesMulti(AnnotationWrapper wrapper) {
        assertEquals(BOOLEAN, wrapper.getPropertyType("booly"));
        assertEquals(BYTE, wrapper.getPropertyType("bytey"));
        assertEquals(CHAR, wrapper.getPropertyType("chary"));
        assertEquals(AnnotationPropertyType.CLASS, wrapper.getPropertyType("classy"));
        assertEquals(DOUBLE, wrapper.getPropertyType("doubly"));
        assertEquals(FLOAT, wrapper.getPropertyType("floaty"));
        assertEquals(INT, wrapper.getPropertyType("inty"));
        assertEquals(LONG, wrapper.getPropertyType("longy"));
        assertEquals(SHORT, wrapper.getPropertyType("shorty"));
        assertEquals(STRING, wrapper.getPropertyType("stringy"));
        assertEquals(ENUM, wrapper.getPropertyType("enumy"));
        assertEquals(ANNOTATION, wrapper.getPropertyType("annoty"));
    }

    @Test
    public void shouldGetMethods() {
        List<Method> methods = type.getMethods();

        assertEquals("methods: " + methods.stream().map(m -> m.getName()).collect(joining("\n")), 3, methods.size());
        assertMethod0(type.getMethod("method0"));
        assertMethod1(type.getMethod("method1"));
        assertMethod2(type.getMethod("method2"));
    }

    private void assertMethod0(Method method) {
        assertEquals("method0", method.getName());
        assertEquals(type, method.getDeclaringType());
        assertEquals(ReflectionType.type(void.class), method.getReturnType());
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
        assertEquals(2, ((A[]) method.getAnnotationWrapper(AA.class).getProperty("value")).length);

        assertTrue(method.isAnnotated(A.class));
        assertEquals(2, method.getAnnotations(A.class).size());
        assertEquals("mmm", method.getAnnotations(A.class).get(0).value());
        assertEquals("nnn", method.getAnnotations(A.class).get(1).value());

        assertEquals(2, method.getAnnotationWrappers(A.class).size());
        assertEquals("mmm", method.getAnnotationWrappers(A.class).get(0).getProperty("value"));
        assertEquals("nnn", method.getAnnotationWrappers(A.class).get(1).getProperty("value"));
        assertThat(catchThrowable(() -> method.getAnnotationWrapper(A.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Found 2 annotations of type " + A.class.getName() + " when expecting only one");

        assertFalse(method.isAnnotated(BB.class)); // @Repeatable

        assertTrue(method.isAnnotated(B.class));
        assertEquals(1, method.getAnnotations(B.class).size());
        assertEquals("bbb", method.getAnnotations(B.class).get(0).value());
        assertEquals(1, method.getAnnotationWrappers(B.class).size());
        assertEquals("bbb", method.getAnnotationWrappers(B.class).get(0).getStringProperty("value"));

        assertEquals(3, method.getAnnotationWrappers().size());
        assertRepeatedAnnotation("mmm", method.getAnnotationWrappers().get(0), A.class);
        assertRepeatedAnnotation("nnn", method.getAnnotationWrappers().get(1), A.class);
        assertRepeatedAnnotation("bbb", method.getAnnotationWrappers().get(2), B.class);
    }

    private void assertRepeatedAnnotation(String value, AnnotationWrapper annotation, Class<?> type) {
        assertEquals(type.getSimpleName(), annotation.getAnnotationType().getSimpleName());
        assertEquals(type.getName(), annotation.getAnnotationType().getFullName());
        assertEquals(1, annotation.getPropertyMap().size());
        assertEquals(value, annotation.getPropertyMap().get("value"));
    }

    private void assertMethod1(Method method) {
        assertEquals("method1", method.getName());
        assertEquals(type, method.getDeclaringType());

        assertMethod1ReturnType(method.getReturnType());
        assertMethod1Annotations(method);

        List<Parameter> parameters = method.getParameters();
        assertMethod1Parameters(method, parameters);
    }

    private void assertMethod1ReturnType(Type returnType) {
        assertTrue(returnType.isA(List.class));
        assertTrue(returnType.isA(Collection.class));
        assertTrue(returnType.isA(Iterable.class));
        assertFalse(returnType.isA(Number.class));
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
        assertEquals("ooo", method.getAnnotationWrappers(A.class).get(0).getStringProperty("value"));
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
        List<AnnotationWrapper> bb = method.getAnnotationWrapper(BB.class).getAnnotationProperties("value");
        assertEquals("b0", bb.get(0).getStringProperty("value"));
        assertEquals("b1", bb.get(1).getStringProperty("value"));
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
        assertEquals(ReflectionType.type(String.class), parameter.getType());

        assertFalse(parameter.isAnnotated(A.class));
        assertEquals(0, parameter.getAnnotations(A.class).size());
        assertEquals(0, parameter.getAnnotationWrappers().size());
    }

    private void assertMethod1Parameter1(Method method, Parameter parameter1) {
        assertEquals(method, parameter1.getMethod());
        assertEquals("bool", parameter1.getName());
        assertEquals(ReflectionType.type(boolean.class), parameter1.getType());

        assertTrue(parameter1.isAnnotated(A.class));
        assertEquals(1, parameter1.getAnnotations(A.class).size());
        assertEquals("ppp", parameter1.getAnnotations(A.class).get(0).value());
        assertEquals(1, parameter1.getAnnotationWrappers().size());

        AnnotationWrapper p1a0 = parameter1.getAnnotationWrappers().get(0);
        assertEquals("A", p1a0.getAnnotationType().getSimpleName());
        assertEquals(A.class.getName(), p1a0.getAnnotationType().getFullName());
        assertEquals(1, p1a0.getPropertyMap().size());
        assertEquals("ppp", p1a0.getPropertyMap().get("value"));
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

    private void assertMethod2(Method method) {
        assertEquals("method2", method.getName());
        assertEquals(type, method.getDeclaringType());
        assertEquals(ReflectionType.type(Nested.class), method.getReturnType());
        assertTrue(method.getReturnType().isA(Nested.class));
        assertTrue(method.getReturnType().isA(Container.class));

        List<Parameter> parameters = method.getParameters();
        assertEquals(0, parameters.size());
    }

    @Test
    public void shouldVisitType() {
        AtomicInteger count = new AtomicInteger();

        type.accept(new TypeVisitor() {
            @Override
            public void visit(Method method) {
                assertThat(method.getName()).matches("method[012]");
                count.getAndIncrement();
            }

            @Override
            public void visit(Field field) {
                assertThat(field.getName()).matches("string|bool|map|fooNum");
                count.getAndIncrement();
            }
        });

        assertEquals(7, count.get());
    }
}
