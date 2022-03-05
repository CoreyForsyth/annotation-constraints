package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationConstraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@AnnotationConstraint(TestAnnotationValidator.class)
public @interface TestAnnotation
{
    int value();
}
