package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationValidator;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;

public class TestAnnotationValidator extends AnnotationValidator<TestAnnotation>
{
    @Override
    public boolean validate(TestAnnotation annotation, Element element, Messager messager)
    {
        return annotation.value() >= 2;
    }
}
