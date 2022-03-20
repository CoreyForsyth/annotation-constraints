import com.annotationconstraints.demo.ExampleAnnotation;
import com.annotationconstraints.demo.ExampleAnnotationAliasValidator;
import com.annotationconstraints.demo.ExampleAnnotationValidator;
import javax.validation.constraints.Pattern;

public class TestDemoAnnotation
{
    /**
     * Set the value to < 1 to fail the {@link ExampleAnnotationValidator}
     * Set the value to > 1 to fail the {@link ExampleAnnotationAliasValidator}
     * Set the regexp value to an invalid regexp to fail the {@link com.annotationconstraints.demo.PatternValidator}
     */
    @ExampleAnnotation(1)
    public void testMethod(@Pattern(regexp = "[a]") String pattern)
    {

    }
}
