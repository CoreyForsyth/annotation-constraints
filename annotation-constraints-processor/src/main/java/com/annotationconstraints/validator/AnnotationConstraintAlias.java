package com.annotationconstraints.validator;

import com.annotationconstraints.AnnotationConstraint;

@AnnotationConstraint(value = AnnotationConstraintValidator.class, aliasFor = "com.annotationconstraints.AnnotationConstraint")
public @interface AnnotationConstraintAlias
{
    String aliasFor() default "";
}
