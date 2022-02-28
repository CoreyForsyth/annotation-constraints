package com.annotationconstraints;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes("com.annotationconstraints.AnnotationConstraint")
public class AnnotationConstraintsProcessor extends AbstractProcessor
{
    public static String GENERATED_PACKAGE = "com.annotationconstraints.generated";
    public static String GENERATED_PROCESSOR_SUFFIX = "Processor";

    private final Set<String> generatedProcessors = new HashSet<>();

    /**
     * Returns the latest supported SourceVersion
     * @return latest supported SourceVersion
     */
    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    /**
     * Process given elements
     * @param annotations TypeElements of user annotations that are annotated with {@link AnnotationConstraint}
     * @param roundEnv Provides methods to query for annotation processing info
     * @return false
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver()) {
            return false;
        }
        Set<? extends Element> userAnnotations = roundEnv.getElementsAnnotatedWith(AnnotationConstraint.class);
        for (Element userAnnotation : userAnnotations) {
            TypeMirror validator = getValidatorTypeMirror(userAnnotation, AnnotationConstraint.class, "value");
            JavaFile javaFile = buildConstraintProcesser(userAnnotation, validator);
            try
            {
                javaFile.writeTo(processingEnv.getFiler());
                generatedProcessors.add(GENERATED_PACKAGE + "." +  userAnnotation.getSimpleName() + GENERATED_PROCESSOR_SUFFIX);
                writeProcessorClass(userAnnotation);
            }
            catch (IOException e)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write the generated annotation processor class", userAnnotation);
            }
        }
        return false;
    }

    /**
     * Retrieve the TypeMirror representation of the targetElement associated to targetAnnotation.
     * The targetElement must be a Class type element.
     * This is necessary because the Class may not be available at compile time.
     * The TypeElement is provided as a replacement to the actual class.
     * @param element The element annotated with targetAnnotation
     * @param targetAnnotation Annotation to extract TypeMirror value
     * @param targetElement Element in Annotation with Class type
     * @return the TypeMirror corresponding to the AnnotationConstraint
     * @see #getValidatorTypeMirrorFromException Used as a backup method
     */
    private static TypeMirror getValidatorTypeMirror(Element element, Class<? extends Annotation> targetAnnotation, String targetElement) {
        return element.getAnnotationMirrors().stream()
            .filter(annotationMirror -> annotationMirror.getAnnotationType().toString().equals(targetAnnotation.getName())).findFirst()
            .map(AnnotationMirror::getElementValues)
            .map(Map::entrySet)
            .orElseGet(HashSet::new).stream()
            .filter(kv -> kv.getKey().getSimpleName().contentEquals(targetElement))
            .findFirst()
            .map(Map.Entry::getValue)
            .map(AnnotationValue::getValue)
            .filter(o -> o instanceof TypeMirror)
            .map(TypeMirror.class::cast)
            .orElseGet(() -> getValidatorTypeMirrorFromException(element, targetAnnotation, targetElement));
    }

    /**
     * Retrieve the TypeMirror representation of the targetElement associated to targetAnnotation.
     * The compiler will throw a MirroredTypeException that contains the TypeMirror
     * @param element The element annotated with targetAnnotation
     * @param targetAnnotation Annotation to extract TypeMirror value
     * @param targetElement Element in Annotation with Class type
     * @return the {@link TypeMirror} corresponding to the {@link AnnotationConstraint}
     * @see #getValidatorTypeMirror for a method that doesn't cause an exception
     */
    private static TypeMirror getValidatorTypeMirrorFromException(Element element, Class<? extends Annotation> targetAnnotation, String targetElement) {
        try
        {
            targetAnnotation.getMethod(targetElement).invoke(element.getAnnotation(targetAnnotation));
        }
        catch(InvocationTargetException ite)
        {
            if (ite.getCause() instanceof MirroredTypeException) {
                return ((MirroredTypeException) ite.getCause()).getTypeMirror();
            }
        }
        catch (NoSuchMethodException | IllegalAccessException ignored)
        {
        }
        return null;
    }

    private static JavaFile buildConstraintProcesser(Element userAnnotation, TypeMirror annotationValidator) {
        String annotationSimpleName = userAnnotation.getSimpleName().toString();
        String annotationName = userAnnotation.toString();
        ClassName annotationToBeProcessedClassName = ClassName.get(annotationName.substring(0, annotationName.lastIndexOf(".")), annotationSimpleName);

        AnnotationSpec supportedAnnotationTypes = AnnotationSpec.builder(SupportedAnnotationTypes.class)
            .addMember("value", CodeBlock.of("\"" + annotationName + "\""))
            .build();

        MethodSpec getSupportedSourceVersion = MethodSpec.methodBuilder("getSupportedSourceVersion")
            .addModifiers(Modifier.PUBLIC)
            .returns(SourceVersion.class)
            .addStatement("return $T.latestSupported()", SourceVersion.class)
            .build();

        MethodSpec process = MethodSpec.methodBuilder("process")
            .addModifiers(Modifier.PUBLIC)
            .returns(boolean.class)
            .addParameter(ParameterizedTypeName.get(ClassName.get(Set.class), WildcardTypeName.subtypeOf(TypeElement.class)), "annotations")
            .addParameter(RoundEnvironment.class, "roundEnv")
            .beginControlFlow("if (roundEnv.processingOver())")
            .addStatement("return true")
            .endControlFlow()
            .addStatement("$1T annotationConstraint = $2T.class.getAnnotation($1T.class)", AnnotationConstraint.class, annotationToBeProcessedClassName)
            .beginControlFlow("for ($T element : roundEnv.getElementsAnnotatedWith($T.class))", Element.class, annotationToBeProcessedClassName)
            .addStatement("$1T testAnnotation = element.getAnnotation($1T.class)", annotationToBeProcessedClassName)
            .addStatement("$1T annotationValidator = new $1T()", annotationValidator)
            .addStatement("annotationValidator.init(processingEnv, element)", annotationValidator)
            .addStatement("boolean validated = annotationValidator.validate(testAnnotation)")
            .beginControlFlow("if (!validated)")
            .addStatement("processingEnv.getMessager().printMessage($T.Kind.ERROR, String.format(\"Failed to validate annotation %s\", testAnnotation), element)", Diagnostic.class)
            .endControlFlow()
            .endControlFlow()
            .addStatement("return true")
            .build();

        TypeSpec userAnnotationProcessor = TypeSpec.classBuilder(annotationSimpleName + GENERATED_PROCESSOR_SUFFIX)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(supportedAnnotationTypes)
            .superclass(AbstractProcessor.class)
            .addMethod(getSupportedSourceVersion)
            .addMethod(process)
            .build();

        return JavaFile.builder(GENERATED_PACKAGE, userAnnotationProcessor).build();
    }

    private void writeProcessorClass(Element originatingElement) {
        try {
            FileObject fileObject = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH,"", "META-INF/services/javax.annotation.processing.Processor");
            new BufferedReader(new InputStreamReader(fileObject.openInputStream()))
                .lines().forEach(generatedProcessors::add);
        }
        catch (IOException ignored)
        {
        }
        try
        {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/services/javax.annotation.processing.Processor", originatingElement);
            PrintWriter printWriter = new PrintWriter(fileObject.openWriter());
            this.generatedProcessors.forEach(printWriter::println);
            printWriter.close();
        }
        catch (IOException e)
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write the Processor config file");
        }
    }


}
