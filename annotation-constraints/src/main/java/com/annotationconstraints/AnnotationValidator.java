package com.annotationconstraints;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * This class is used to provide a mechanism to validate an annotation's elements
 * @param <T> The user annotation to be validated. This annotation must be annotated with {@link AnnotationConstraint}
 */
public abstract class AnnotationValidator<T extends Annotation>
{
    protected ProcessingEnvironment processingEnv;
    protected Element element;

    /**
     * Called before {@link #validate} to provide the processing env and the element
     * @param processingEnv The ProcessingEnvironment provided for annotation processing
     * @param element The Element corresponding to the user's annotation T
     */
    final public void init(ProcessingEnvironment processingEnv, Element element)
    {
        this.processingEnv = processingEnv;
        this.element = element;
    }

    /**
     * This method is called during annotation processing to validate an annotation's parameters.
     * If true is returned, the annotation is valid and the compilation may continue.
     * If false is returned, the annotation is invalid and the compilation will stop.
     * An error message will be returned on the element.
     * <p>The ProcessingEnvironment and the Element may be used in the validation, and to provide
     * enhanced messaging to the user.
     * They are provided as a way to provide more information to the user during compilation if required.
     * @param annotation The instance of user annotation that is to be validated
     * @return false if the annotation is invalid, true otherwise
     * @see javax.annotation.processing.Processor#process(Set, RoundEnvironment)
     */
    public boolean validate(T annotation)
    {
        return true;
    }
}
