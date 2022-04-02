package com.annotationconstraints.generated;

import com.annotationconstraints.validator.AnnotationConstraintAlias;
import com.annotationconstraints.validator.AnnotationConstraintValidator;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.annotationconstraints.AnnotationConstraint")
public final class AnnotationConstraintAliasProcessor extends AbstractProcessor
{
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver())
        {
            return true;
        }
        for (TypeElement annotation : annotations)
        {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation))
            {
                AnnotationMirror annotationMirror = getAnnotationMirror(element, "com.annotationconstraints.AnnotationConstraint");
                Elements elements = processingEnv.getElementUtils();
                AnnotationConstraintAlias testAnnotation = new AnnotationConstraintAlias()
                {
                    @Override
                    public Class<? extends Annotation> annotationType()
                    {
                        return AnnotationConstraintAlias.class;
                    }

                    @Override
                    public String aliasFor()
                    {
                        return (String) getAnnotationValue(annotationMirror, "aliasFor", elements);
                    }
                };
                AnnotationConstraintValidator annotationValidator = new AnnotationConstraintValidator();
                annotationValidator.init(processingEnv, element);
                boolean validated = annotationValidator.validate(testAnnotation);
                if (!validated)
                {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Failed to validate annotation %s with validator %s", annotationMirror, AnnotationConstraintValidator.class), element);
                }
            }
        }
        return false;
    }

    public static Object getAnnotationValue(AnnotationMirror annotationMirror, String targetElement,
                                            Elements elements)
    {
        return Optional.of(annotationMirror)
            .map(elements::getElementValuesWithDefaults)
            .map(Map::entrySet)
            .orElse(Collections.emptySet()).stream()
            .filter(kv -> kv.getKey().getSimpleName().contentEquals(targetElement))
            .findFirst()
            .map(Map.Entry::getValue)
            .map(AnnotationValue::getValue).orElse(null);
    }

    public static AnnotationMirror getAnnotationMirror(Element element, String targetAnnotation)
    {
        return element.getAnnotationMirrors().stream()
            .filter(am -> am.getAnnotationType().toString().equals(targetAnnotation))
            .findFirst().orElse(null);
    }
}
