package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationValidator;

public class TestAnnotationValidator extends AnnotationValidator<TestAnnotation>
{
    /**
     * This simple implementation of {@link AnnotationValidator<TestAnnotation>} validates
     * the value is >= 2
     * @param annotation The instance of user annotation that is to be validated
     * @return true if the value is >= 2
     */
    @Override
    public boolean validate(TestAnnotation annotation)
    {
        return annotation.value() >= 2;
    }
}
