# ExAP - Extended Annotation Processor

The [Java Annotation Processor API](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/Processor.html) is a powerful tool to execute custom code when `javac` runs. But the API is ... quite implementation oriented. This project tries to provide an alternative API that is more for the writers of annotation processors.

For example, to mark an type with an error, in the standard API you'd have to:

```java
	processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "this is wrong", element);
```

The extended annotation processor offers this functionality directly at the element:

```java
	element.error("this is wrong");
```

See the test module for a working example.

## Unit Testing

The second big feature is that you can unit-test most of your annotation processor. While this can not cover 100% (e.g. there is no way to access the javadoc comments at runtime), it's very helpful to have short feedback cycles for the parts that you work most at. See the `ReflectionTest` class for an example.

## Status

This project still has some way to go to be considered feature complete. But it may already prove useful to most annotation processor projects you have at hand.
