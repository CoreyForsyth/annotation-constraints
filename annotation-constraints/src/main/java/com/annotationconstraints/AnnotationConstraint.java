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
     * @return An implementation class of AnnotationValidator
     */
    Class<? extends AnnotationValidator<?>> value();

    /**
     * @return The name of annotation this is an alias for, otherwise blank
     * Any elements defined in this annotation much match the original annotation exactly
     */
    String aliasFor() default "";
}
