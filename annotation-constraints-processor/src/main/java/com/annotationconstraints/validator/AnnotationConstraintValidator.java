package com.annotationconstraints.validator;

import com.annotationconstraints.AnnotationValidator;
import java.util.regex.Pattern;

public class AnnotationConstraintValidator extends AnnotationValidator<AnnotationConstraintAlias>
{
    private static final String ID_PATTERN = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
    private static final Pattern FQCN = Pattern.compile(ID_PATTERN + "(\\." + ID_PATTERN + ")*");

    @Override
    public boolean validate(AnnotationConstraintAlias annotation)
    {
        System.out.println("AnnotationConstraint ");
        return annotation.aliasFor().equals("") || FQCN.matcher(annotation.aliasFor()).matches();
    }
}
