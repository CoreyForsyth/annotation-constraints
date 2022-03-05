package com.annotationconstraints.demo;

import java.lang.annotation.Annotation;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TestAnnotationValidatorTest
{

    @Test
    void testAnnotationValidatorValid()
    {
        TestAnnotationValidator testAnnotationValidator = new TestAnnotationValidator();
        TestAnnotation testAnnotation = new TestAnnotation()
        {
            @Override
            public Class<? extends Annotation> annotationType()
            {
                return TestAnnotation.class;
            }

            @Override
            public int value()
            {
                return 1;
            }
        };
        boolean isValidAnnotation = testAnnotationValidator.validate(testAnnotation);
        assertTrue(isValidAnnotation);
    }

    @Test
    void testAnnotationValidatorInvalid()
    {
        TestAnnotationValidator testAnnotationValidator = new TestAnnotationValidator();
        TestAnnotation testAnnotation = new TestAnnotation()
        {
            @Override
            public Class<? extends Annotation> annotationType()
            {
                return TestAnnotation.class;
            }

            @Override
            public int value()
            {
                return 0;
            }
        };
        boolean isValidAnnotation = testAnnotationValidator.validate(testAnnotation);
        assertFalse(isValidAnnotation);
    }

}