package com.annotationconstraints.demo;

import com.annotationconstraints.AnnotationValidator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PatternValidator extends AnnotationValidator<PatternAlias>
{
    @Override
    public boolean validate(PatternAlias annotation)
    {
        try
        {
            Pattern.compile(annotation.regexp());
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
