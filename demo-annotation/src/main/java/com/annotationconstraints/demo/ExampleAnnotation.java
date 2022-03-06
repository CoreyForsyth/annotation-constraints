package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationConstraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@AnnotationConstraint(ExampleAnnotationValidator.class)
public @interface ExampleAnnotation
{
    int value();
}
