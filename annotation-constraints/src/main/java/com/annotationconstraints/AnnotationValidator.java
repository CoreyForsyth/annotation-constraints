package com.annotationconstraints;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * This class is used to provide a mechanism to validate an annotation's parameters
 * @param <T> The user annotation to be validated. This annotation must be annotated with {@link AnnotationConstraint}
 */
public abstract class AnnotationValidator<T extends Annotation>
{
    /**
     * This method is called during annotation processing to validate an annotation's parameters.
     * If true is returned, the annotation is valid and the compilation may continue.
     * If false is returned, the annotation is invalid and the compilation will stop.
     * An error message will be returned on the element.
     * <p>Both element and messager can be ignored if they are not needed.
     * They are provided as a way to provide more information to the user during compilation if required.
     * @param annotation The instance of user annotation that is to be validated
     * @param element The element corresponding to the user annotation
     * @param messager The messager to be used if the user would like to manually add messages.
     * @return false if the annotation is invalid, true otherwise
     * @see javax.annotation.processing.Processor#process(Set, RoundEnvironment)
     */
    public boolean validate(T annotation, Element element, Messager messager)
    {
        return true;
    }
}
