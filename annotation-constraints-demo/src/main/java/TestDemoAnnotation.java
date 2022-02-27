import com.annotationconstraints.demo.TestAnnotation;

public class TestDemoAnnotation
{
    public static void main(String[] args) {
        testMethod();
    }

    @TestAnnotation(3)
    private static void testMethod() {
    }
}
