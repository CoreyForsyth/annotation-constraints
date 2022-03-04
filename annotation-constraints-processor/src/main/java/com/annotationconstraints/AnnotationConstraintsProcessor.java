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
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
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
     *
     * @return latest supported SourceVersion
     */
    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    /**
     * Process given elements
     *
     * @param annotations TypeElements of user annotations that are annotated with {@link AnnotationConstraint}
     * @param roundEnv    Provides methods to query for annotation processing info
     * @return false
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver())
        {
            return false;
        }
        Set<? extends Element> userAnnotations = roundEnv.getElementsAnnotatedWith(AnnotationConstraint.class);
        for (Element userAnnotation : userAnnotations)
        {
            TypeMirror validator = AnnotationUtil.getTypeMirrorFromAnnotationElement(userAnnotation, AnnotationConstraint.class, "value");
            JavaFile javaFile = buildConstraintProcesser(userAnnotation, validator);
            try
            {
                javaFile.writeTo(processingEnv.getFiler());
                generatedProcessors.add(GENERATED_PACKAGE + "." + userAnnotation.getSimpleName() + GENERATED_PROCESSOR_SUFFIX);
                writeProcessorClass(userAnnotation);
            }
            catch (IOException e)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write the generated annotation processor class", userAnnotation);
            }
        }
        return false;
    }


    private static JavaFile buildConstraintProcesser(Element userAnnotation, TypeMirror annotationValidator)
    {
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
            .beginControlFlow("for ($T annotation : annotations)", TypeElement.class)
            .beginControlFlow("for ($T element : roundEnv.getElementsAnnotatedWith(annotation))", Element.class)
            .addStatement("$1T testAnnotation = element.getAnnotation($1T.class)", annotationToBeProcessedClassName)
            .addStatement("$1T annotationValidator = new $1T()", annotationValidator)
            .addStatement("annotationValidator.init(processingEnv, element)", annotationValidator)
            .addStatement("boolean validated = annotationValidator.validate(testAnnotation)")
            .beginControlFlow("if (!validated)")
            .addStatement("processingEnv.getMessager().printMessage($T.Kind.ERROR, $T.format(\"Failed to validate annotation %s with validator %s\", testAnnotation, $T.class), element)",
                Diagnostic.class, String.class, annotationValidator)
            .endControlFlow()
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

    private void writeProcessorClass(Element originatingElement)
    {
        try
        {
            FileObject fileObject = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, "", "META-INF/services/javax.annotation.processing.Processor");
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
