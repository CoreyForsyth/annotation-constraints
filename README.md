# Annotation Constraints

An annotation processor that allows you to add compile-time validation to any annotation.

It works by generating a separate annotation processor for every user-defined annotation that are annotated with an `@AnnotationConstraint`.

## Purpose

When writing an annotation, the elements you can use are limited to these java types:
- Primitives
- String
- Class
- Enum
- Annotation
- Array of one of the above

So long as the value a user selects is a constant with the correct type, the value can be anything. 
This can be an issue if there are inherent constraints for a value of an annotation.

A simple example would be the `@Pattern` annotation from Java/Jakarta EE.
This annotation takes in a string that must be a valid regular expression.
This library allows you to add a compile-time check, to ensure the pattern is a valid regular expression.
An example is provided in demo-annotation that implements this functionality.


## Usage
The `@AnnotationConstraint` annotation is used to set the user defined validator.

Here is an annotation called `@ExampleAnnotation` that is validated using the `ExampleAnnotationValidator` class.
```java
@AnnotationConstraint(ExampleAnnotationValidator.class)
public @interface ExampleAnnotation
{
    // Must be a positive integer
    int value();
}
```
The definition of `ExampleAnnotationValidator`, which checks if the value is positive:
```java
public class ExampleAnnotationValidator extends AnnotationValidator<ExampleAnnotation>
{
    @Override
    public boolean validate(ExampleAnnotation annotation)
    {
        return annotation.value() > 0;
    }
}
```
Now, when a user of `ExampleAnnotation` attempts to use it in an 'invalid' way, compilation will fail. Example:
```java
public class TestValidator
{
    // Compilation will fail since -1 is not a positive integer
    @ExampleAnnotation(-1)
    public void test()
    {
        //...
    }
}
```
That's all there is to it! 

An annotation validator can be as complicated as you need.

## How it works
The annotation-constraints-processor lib contains an annotation processor.
It finds any annotations that are themselves annotated with `@AnnotationConstraint`, and generates annotation processors for each one.
Just like [AutoService](https://github.com/google/auto/tree/master/service), it adds the generated annotation processor to `META-INF/services/javax.annotation.processor`.

# Setup
In the library that you want to add annotation constraints to, import 'com.annotationconstraints:annotation-constraints-processor' as an annotation processor.

'com.annotationconstraints:annotation-constraints' contains the core annotation and must be included in the compile and runtime classpath.

Gradle example:
```groovy
compileOnly 'com.annotationconstraints:annotation-constraints-processor:1.0.0-SNAPSHOT'
annotationProcessor 'com.annotationconstraints:annotation-constraints-processor:1.0.0-SNAPSHOT'
implementation 'com.annotationconstraints:annotation-constraints:1.0.0-SNAPSHOT'
```
Maven example:
```xml
<dependencies>
  <dependency>
    <groupId>com.annotationconstraints</groupId>
    <artifactId>annotation-constraints</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```
```xml
<plugins>
  <plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
    <annotationProcessorPaths>
      <path>
        <groupId>com.annotationconstraints</groupId>
        <artifactId>annotation-constraints-processor</artifactId>
        <version>1.0.0</version>
      </path>
    </annotationProcessorPaths>
    </configuration>
  </plugin>
</plugins>
```
Now, you can create your own annotation validators.
Users of your library just need to add it as an annotation processor, like above.

# Annotation Alias

This library is designed to add validation directly to the definition of an annotation.

It also provides a way to add validation to any third-party annotations. A user can create an alias annotation that is an alias for a third-party target annotation.
A given example is the `@PatternAlias` and `@PatternValidator` in demo-annotation, which validates the regular expression in the third-party `@Pattern` annotation.

This is accomplished through the `aliasFor` string in `@AnnotationConstraint`. It is the fully qualified classname for the target annotation.
The target annotation's values are mirrored to the alias annotation. The user can validate the alias annotation as if it were the target annotation.
