package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationConstraint;

@AnnotationConstraint(value = ExampleAnnotationAliasValidator.class, aliasFor = "com.annotationconstraints.demo.ExampleAnnotation")
public @interface ExampleAnnotationAlias
{
    int value();
}
