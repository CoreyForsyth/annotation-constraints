package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationValidator;

public class ExampleAnnotationAliasValidator extends AnnotationValidator<ExampleAnnotationAlias>
{
    /**
     * This simple implementation of {@link AnnotationValidator} validates
     * the value of any {@link ExampleAnnotation} is {@literal <} 2
     * It uses an alias annotation to accomplish this.
     *
     * @param annotation The instance of alias annotation that is to be validated
     * @return true if the value is {@literal <} 2
     */
    @Override
    public boolean validate(ExampleAnnotationAlias annotation)
    {
        return annotation.value() < 2;
    }
}
