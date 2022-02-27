package com.annotationconstraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface AnnotationConstraint
{
    /**
     * @return An implementation of AnnotationValidator
     */
    Class<? extends AnnotationValidator<?>> value();
}
