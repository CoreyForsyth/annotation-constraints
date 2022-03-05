package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationValidator;

public class TestAnnotationValidator extends AnnotationValidator<TestAnnotation>
{
    /**
     * This simple implementation of {@link AnnotationValidator<TestAnnotation>} validates
     * the value of any {@link TestAnnotation} is > 0
     *
     * @param annotation The instance of user annotation that is to be validated
     * @return true if the value is > 0
     */
    @Override
    public boolean validate(TestAnnotation annotation)
    {
        return annotation.value() > 0;
    }
}
