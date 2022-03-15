package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationValidator;

public class ExampleAnnotationValidator extends AnnotationValidator<ExampleAnnotation>
{
    /**
     * This simple implementation of {@link AnnotationValidator} validates
     * the value of any {@link ExampleAnnotation} is {@literal >} 0
     *
     * @param annotation The instance of user annotation that is to be validated
     * @return true if the value is {@literal >} 0
     */
    @Override
    public boolean validate(ExampleAnnotation annotation)
    {
        return annotation.value() > 0;
    }
}
