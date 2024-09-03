import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

/**
 * Unit tests for Add method declaration recipe.
 * TODO: add more tests for full test coverage.
 * @author Annabelle Mittendorf Smith
 */

public class AddMethodDeclarationTest implements RewriteTest {

    /**
     * This method defines recipes used for testing.
     * @param spec stores settings for testing environment.
     */
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipes(
                new com.azure.recipes.v2recipes.AddMethodDeclaration(
                        "TestDefault.MyClass","void myMethod() {}"
                ),
                new com.azure.recipes.v2recipes.AddMethodDeclaration(
                        "NotValid.MyClass","void myMethod() { boo }"
                ),
                new com.azure.recipes.v2recipes.AddMethodDeclaration(
                        "TestModified.MyClass","void myMethod() {}"
                )
        );
    }

    // Helper variables
    public String lineClassDeclaration = "public class MyClass {\n";
    public String lineClassClose = "}\n";

    /**
     * Test method: asserts empty void method added.
     */
    @Test
    public void testAddMethodDeclaration_Default() {
        @Language("java") String before = "package TestDefault;"
                + lineClassDeclaration + lineClassClose;

        @Language("java") String after = "package TestDefault;"
                + lineClassDeclaration
                + "    void myMethod() {\n"
                + "    }\n"
                + lineClassClose;
        rewriteRun(java(before,after));
    }

    /**
     * Test method: asserts no method is created if template is invalid.
     */
    @Test
    public void testInvalidMethodNotAdded() {
        @Language("java") String unchanged = "package NotValid;\n"
                + lineClassDeclaration + lineClassClose;

        rewriteRun(java(unchanged));
    }

}
