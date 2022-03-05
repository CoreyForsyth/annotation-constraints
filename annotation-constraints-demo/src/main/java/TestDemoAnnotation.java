import com.annotationconstraints.demo.TestAnnotation;

public class TestDemoAnnotation
{
    /**
     * Set the value to < 1 to fail the {@link com.annotationconstraints.demo.TestAnnotationValidator}
     * Set the value to > 1 to fail the {@link com.annotationconstraints.demo.TestAliasAnnotationValidator}
     */
    @TestAnnotation(1)
    public void testMethod()
    {

    }
}
