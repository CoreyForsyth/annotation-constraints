
package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationConstraint;

@AnnotationConstraint(value = PatternValidator.class, aliasFor = "javax.validation.constraints.Pattern")
public @interface PatternAlias
{
    String regexp();
}
