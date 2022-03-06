# Annotation Constraints

An annotation processor that allows you to add compile-time validation to any annotation.

## Example
The `@AnnotationConstraint` annotation is used to point to a user defined validator.

If you define an annotation in which the element must be a positive integer:
```java
@AnnotationConstraint(ExampleAnnotationValidator.class)
public @interface ExampleAnnotation
{
    // Must be a positive integer
    int value();
}
```
The annotation is considered valid only when the value is a positive integer.
Below is an example validator for a positive integer:
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
    @ExampleAnnotation(-1)
    public void test()
    {
        // Compilation will fail since -1 is not a positive integer
    }
}
```
That's all there is to it! 

An annotation validator can be as complicated as you need.

## How it works
The annotation-constraints-processor contains an annotation processor.
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
    <version>1.0.0-SNAPSHOT</version>
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
        <version>1.0.0-SNAPSHOT</version>
      </path>
    </annotationProcessorPaths>
    </configuration>
  </plugin>
</plugins>
```
Now, you can create your own annotation validators.
Users of your library just need to add it as an annotation processor, like above.