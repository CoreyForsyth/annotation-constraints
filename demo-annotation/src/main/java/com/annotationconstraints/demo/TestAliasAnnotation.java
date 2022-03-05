package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationConstraint;

@AnnotationConstraint(value = TestAliasAnnotationValidator.class, aliasFor = "com.annotationconstraints.demo.TestAnnotation")
public @interface TestAliasAnnotation
{
    int value();
}
