package com.annotationconstraints;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class AnnotationUtil
{

    /**
     * Retrieve the TypeMirror representation of the targetElement associated to targetAnnotation.
     * The targetElement must be a Class type element.
     * This is necessary because the Class may not be available at compile time.
     * The TypeElement is provided as a replacement to the actual class.
     *
     * @param element          The element annotated with targetAnnotation
     * @param targetAnnotation Annotation to extract TypeMirror value
     * @param targetElement    Element in Annotation with Class type
     * @return the TypeMirror corresponding to the AnnotationConstraint
     * @see #getTypeMirrorFromException Used as a backup method
     */
    public static TypeMirror getTypeMirrorFromAnnotationElement(Element element, Class<? extends Annotation> targetAnnotation, String targetElement)
    {
        AnnotationMirror annotationMirror = getAnnotationMirror(element, targetAnnotation.getName());
        Object annotationValue = getAnnotationValue(annotationMirror, targetElement);
        return annotationValue instanceof TypeMirror ? (TypeMirror) annotationValue : getTypeMirrorFromException(element, targetAnnotation, targetElement);
    }

    /**
     * Retrieve the TypeMirror representation of the targetElement associated to targetAnnotation.
     * The compiler will throw a MirroredTypeException that contains the TypeMirror
     *
     * @param element          The element annotated with targetAnnotation
     * @param targetAnnotation Annotation to extract TypeMirror value
     * @param targetElement    Element in Annotation with Class type
     * @return the {@link TypeMirror} corresponding to the {@link AnnotationConstraint}
     * @see #getTypeMirrorFromAnnotationElement for a method that doesn't cause an exception
     */
    public static TypeMirror getTypeMirrorFromException(Element element, Class<? extends Annotation> targetAnnotation, String targetElement)
    {
        try
        {
            targetAnnotation.getMethod(targetElement).invoke(element.getAnnotation(targetAnnotation));
        }
        catch (InvocationTargetException ite)
        {
            if (ite.getCause() instanceof MirroredTypeException)
            {
                return ((MirroredTypeException) ite.getCause()).getTypeMirror();
            }
        }
        catch (NoSuchMethodException | IllegalAccessException ignored)
        {
        }
        return null;
    }

    /**
     * Retrieve the AnnotationMirror of the targetAnnotation on the element
     * Useful for when the target annotation is not on the classpath,
     * and when retrieving a class from an annotation during processing
     *
     * @param element          An element annotated with targetAnnotation
     * @param targetAnnotation An annotation
     * @return AnnotationMirror of targetAnnotation
     */
    public static AnnotationMirror getAnnotationMirror(Element element, String targetAnnotation)
    {
        return element.getAnnotationMirrors().stream()
            .filter(am -> am.getAnnotationType().toString().equals(targetAnnotation))
            .findFirst().orElse(null);
    }

    /**
     * Iterate through the elements of annotationMirror for targetElement
     *
     * @param annotationMirror An AnnotationMirror
     * @param targetElement    An element of annotationMirror to
     * @return Value of the element of annotationMirror with name targetElement
     */
    public static Object getAnnotationValue(AnnotationMirror annotationMirror, String targetElement)
    {
        return Optional.of(annotationMirror)
            .map(AnnotationMirror::getElementValues)
            .map(Map::entrySet)
            .orElseGet(HashSet::new).stream()
            .filter(kv -> kv.getKey().getSimpleName().contentEquals(targetElement))
            .findFirst()
            .map(Map.Entry::getValue)
            .map(AnnotationValue::getValue).orElse(null);
    }


}
