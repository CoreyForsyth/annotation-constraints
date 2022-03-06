package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationValidator;

public class ExampleAnnotationValidator extends AnnotationValidator<ExampleAnnotation>
{
    /**
     * This simple implementation of {@link AnnotationValidator<ExampleAnnotation>} validates
     * the value of any {@link ExampleAnnotation} is > 0
     *
     * @param annotation The instance of user annotation that is to be validated
     * @return true if the value is > 0
     */
    @Override
    public boolean validate(ExampleAnnotation annotation)
    {
        return annotation.value() > 0;
    }
}
