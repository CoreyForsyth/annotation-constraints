package com.annotationconstraints.demo;

import java.lang.annotation.Annotation;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ExampleAnnotationValidatorTest
{

    @Test
    void testAnnotationValidatorValid()
    {
        ExampleAnnotationValidator exampleAnnotationValidator = new ExampleAnnotationValidator();
        ExampleAnnotation testAnnotation = new ExampleAnnotation()
        {
            @Override
            public Class<? extends Annotation> annotationType()
            {
                return ExampleAnnotation.class;
            }

            @Override
            public int value()
            {
                return 1;
            }
        };
        boolean isValidAnnotation = exampleAnnotationValidator.validate(testAnnotation);
        assertTrue(isValidAnnotation);
    }

    @Test
    void testAnnotationValidatorInvalid()
    {
        ExampleAnnotationValidator exampleAnnotationValidator = new ExampleAnnotationValidator();
        ExampleAnnotation exampleAnnotation = new ExampleAnnotation()
        {
            @Override
            public Class<? extends Annotation> annotationType()
            {
                return ExampleAnnotation.class;
            }

            @Override
            public int value()
            {
                return 0;
            }
        };
        boolean isValidAnnotation = exampleAnnotationValidator.validate(exampleAnnotation);
        assertFalse(isValidAnnotation);
    }

}