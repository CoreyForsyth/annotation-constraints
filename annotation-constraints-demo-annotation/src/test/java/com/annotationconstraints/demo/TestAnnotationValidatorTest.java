package com.annotationconstraints.demo;

import java.lang.annotation.Annotation;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TestAnnotationValidatorTest
{

    @Test
    void testAnnotationValidatorValid()
    {
        TestAnnotationValidator testAnnotationValidator = new TestAnnotationValidator();
        TestAnnotation testAnnotation = new TestAnnotation(){
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
        assertFalse(isValidAnnotation);
    }

    @Test
    void testAnnotationValidatorInvalid()
    {
        TestAnnotationValidator testAnnotationValidator = new TestAnnotationValidator();
        TestAnnotation testAnnotation = new TestAnnotation(){
            @Override
            public Class<? extends Annotation> annotationType()
            {
                return TestAnnotation.class;
            }
            @Override
            public int value()
            {
                return 2;
            }
        };
        boolean isValidAnnotation = testAnnotationValidator.validate(testAnnotation);
        assertTrue(isValidAnnotation);
    }
    
}