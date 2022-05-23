package com.annotationconstraints.processor;

import com.annotationconstraints.AnnotationConstraint;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes("com.annotationconstraints.AnnotationConstraint")
public class AnnotationConstraintProcessor extends AbstractProcessor
{
    public static String GENERATED_PACKAGE = "com.annotationconstraints.generated";
    public static String GENERATED_PROCESSOR_SUFFIX = "Processor";

    private final Set<String> generatedProcessors = new HashSet<>();

    private static MethodSpec getAnnotationValueMethod()
    {
        return MethodSpec.methodBuilder("getAnnotationValue")
            .addParameter(AnnotationMirror.class, "annotationMirror")
            .addParameter(String.class, "targetElement")
            .addParameter(Elements.class, "elements")
            .addCode("return $1T.of(annotationMirror)\n" +
                "  .map(elements::getElementValuesWithDefaults)\n" +
                "  .map($2T::entrySet)\n" +
                "  .orElse($3T.emptySet()).stream()\n" +
                "  .filter(kv -> kv.getKey().getSimpleName().contentEquals(targetElement))\n" +
                "  .findFirst()\n" +
                "  .map($2T.Entry::getValue)\n" +
                "  .map($4T::getValue).orElse(null);", Optional.class, Map.class, Collections.class, AnnotationValue.class)
            .returns(Object.class)
            .addModifiers(Modifier.STATIC)
            .addModifiers(Modifier.PUBLIC)
            .build();
    }

    private static MethodSpec getAnnotationMirrorMethod()
    {
        return MethodSpec.methodBuilder("getAnnotationMirror")
            .addParameter(Element.class, "element")
            .addParameter(String.class, "targetAnnotation")
            .addCode("return element.getAnnotationMirrors().stream()\n" +
                "  .filter(am -> am.getAnnotationType().toString().equals(targetAnnotation))\n" +
                "  .findFirst().orElse(null);", Optional.class, AnnotationMirror.class, Map.class, Collections.class, AnnotationValue.class)
            .returns(AnnotationMirror.class)
            .addModifiers(Modifier.STATIC)
            .addModifiers(Modifier.PUBLIC)
            .build();
    }

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
            writeProcessorClass();
            return false;
        }
        Set<? extends Element> userAnnotations = roundEnv.getElementsAnnotatedWith(AnnotationConstraint.class);
        for (Element userAnnotation : userAnnotations)
        {
            PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(userAnnotation);

            if (packageOf.getSimpleName().toString().equals("")) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    String.format("User annotation with @AnnotationConstraint (@%s) cannot be in the default package.", userAnnotation.getSimpleName()), userAnnotation);
                return false;
            }
            AnnotationConstraint annotationConstraint = userAnnotation.getAnnotation(AnnotationConstraint.class);
            TypeMirror validator = AnnotationUtil.getTypeMirrorFromAnnotationElement(userAnnotation, AnnotationConstraint.class, "value");
            JavaFile javaFile = buildConstraintProcessor(userAnnotation, validator, annotationConstraint.aliasFor());
            try
            {
                javaFile.writeTo(processingEnv.getFiler());
                generatedProcessors.add(GENERATED_PACKAGE + "." + userAnnotation.getSimpleName() + GENERATED_PROCESSOR_SUFFIX);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write the generated annotation processor class", userAnnotation);
            }
        }
        return false;
    }

    private JavaFile buildConstraintProcessor(Element userAnnotation, TypeMirror annotationValidator, String original)
    {
        boolean isAliasAnnotation = !original.equals("");
        String annotationSimpleName = userAnnotation.getSimpleName().toString();
        String annotationName = userAnnotation.toString();
        ClassName annotationToBeProcessedClassName = ClassName.get(annotationName.substring(0, annotationName.lastIndexOf(".")), annotationSimpleName);
        String annotationToProcess = isAliasAnnotation ? original : annotationName;

        AnnotationSpec supportedAnnotationTypes = AnnotationSpec.builder(SupportedAnnotationTypes.class)
            .addMember("value", CodeBlock.of("\"" + annotationToProcess + "\""))
            .build();

        MethodSpec getSupportedSourceVersion = MethodSpec.methodBuilder("getSupportedSourceVersion")
            .addModifiers(Modifier.PUBLIC)
            .returns(SourceVersion.class)
            .addStatement("return $T.latestSupported()", SourceVersion.class)
            .build();

        MethodSpec.Builder builder = MethodSpec.methodBuilder("process")
            .addModifiers(Modifier.PUBLIC)
            .returns(boolean.class)
            .addParameter(ParameterizedTypeName.get(ClassName.get(Set.class), WildcardTypeName.subtypeOf(TypeElement.class)), "annotations")
            .addParameter(RoundEnvironment.class, "roundEnv")
            .beginControlFlow("if (roundEnv.processingOver())")
            .addStatement("return true")
            .endControlFlow()
            .beginControlFlow("for ($T annotation : annotations)", TypeElement.class)
            .beginControlFlow("for ($T element : roundEnv.getElementsAnnotatedWith(annotation))", Element.class);

        if (isAliasAnnotation)
        {
            builder.addStatement("$T annotationMirror = getAnnotationMirror(element, $S)", AnnotationMirror.class, original);
            builder.addStatement("$T elements = processingEnv.getElementUtils()", Elements.class);
            TypeSpec.Builder aliasBuilder = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(annotationToBeProcessedClassName)
                .addMethod(MethodSpec.methodBuilder("annotationType")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Annotation.class)))
                    .addStatement("return $T.class", annotationToBeProcessedClassName).build());
            userAnnotation.getEnclosedElements().forEach(element -> {
                ExecutableElement executableElement = (ExecutableElement) element;
                aliasBuilder.addMethod(MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(executableElement.getReturnType()))
                    .addStatement("return ($T) getAnnotationValue(annotationMirror, $S, elements)", executableElement.getReturnType(), executableElement.getSimpleName().toString())
                    .build());
            });
            builder.addStatement("$T testAnnotation = $L", annotationToBeProcessedClassName, aliasBuilder.build());
        }
        else
        {
            builder.addStatement("$1T testAnnotation = element.getAnnotation($1T.class)", annotationToBeProcessedClassName);
        }

        builder.addStatement("$1T annotationValidator = new $1T()", annotationValidator)
            .addStatement("annotationValidator.init(processingEnv, element)", annotationValidator)
            .addStatement("boolean validated = annotationValidator.validate(testAnnotation)")
            .beginControlFlow("if (!validated)")
            .addStatement("processingEnv.getMessager().printMessage($T.Kind.ERROR, $T.format(\"Failed to validate annotation %s with validator %s\", " +
                    (isAliasAnnotation ? "annotationMirror" : "testAnnotation") +
                    ", $T.class), element)",
                Diagnostic.class, String.class, annotationValidator)
            .endControlFlow()
            .endControlFlow()
            .endControlFlow()
            .addStatement("return false");

        List<MethodSpec> methodSpecs = new ArrayList<>();
        methodSpecs.add(getSupportedSourceVersion);
        methodSpecs.add(builder.build());
        if (isAliasAnnotation) {
            methodSpecs.add(getAnnotationValueMethod());
            methodSpecs.add(getAnnotationMirrorMethod());
        }

        TypeSpec userAnnotationProcessor = TypeSpec.classBuilder(annotationSimpleName + GENERATED_PROCESSOR_SUFFIX)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(supportedAnnotationTypes)
            .superclass(AbstractProcessor.class)
            .addMethods(methodSpecs)
            .build();

        return JavaFile.builder(GENERATED_PACKAGE, userAnnotationProcessor).build();
    }

    private void writeProcessorClass()
    {
        try
        {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/services/javax.annotation.processing.Processor");
            PrintWriter printWriter = new PrintWriter(fileObject.openWriter());
            this.generatedProcessors.forEach(printWriter::println);
            printWriter.close();
        }
        catch (IOException e)
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write the Processor config file. Ensure there isn't a preexisting META-INF/services/javax.annotation.processing.Processor");
        }
    }


}
