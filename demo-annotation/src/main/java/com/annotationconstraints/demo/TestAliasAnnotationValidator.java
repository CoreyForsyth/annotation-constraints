package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationValidator;

public class TestAliasAnnotationValidator extends AnnotationValidator<TestAliasAnnotation>
{
    /**
     * This simple implementation of {@link AnnotationValidator<TestAliasAnnotation>} validates
     * the value of any {@link TestAnnotation} is < 2
     * It uses an alias annotation to accomplish this.
     *
     * @param annotation The instance of alias annotation that is to be validated
     * @return true if the value is < 2
     */
    @Override
    public boolean validate(TestAliasAnnotation annotation)
    {
        return annotation.value() < 2;
    }
}
