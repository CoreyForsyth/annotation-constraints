import com.annotationconstraints.demo.TestAnnotation;
import javax.validation.constraints.Pattern;

public class TestDemoAnnotation
{
    /**
     * Set the value to < 1 to fail the {@link com.annotationconstraints.demo.TestAnnotationValidator}
     * Set the value to > 1 to fail the {@link com.annotationconstraints.demo.TestAliasAnnotationValidator}
     * Set the regexp value to an invalid regex to fail the {@link com.annotationconstraints.demo.PatternValidator}
     */
    @TestAnnotation(1)
    @Pattern(regexp = "[a]")
    public void testMethod()
    {

    }
}
