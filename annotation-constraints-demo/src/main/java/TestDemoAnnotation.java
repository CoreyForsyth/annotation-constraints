import com.annotationconstraints.demo.TestAnnotation;

public class TestDemoAnnotation
{
    // Change the value to >= 2 to pass validation
    @TestAnnotation(2)
    public void testMethod() {

    }
}
