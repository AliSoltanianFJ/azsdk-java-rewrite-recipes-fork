import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

/**
 * Unit tests for DeleteMethod recipe.
 * TODO: add tests for deleting implementations
 * @author Annabelle Mittendorf Smith
 */

public class DeleteMethodTest implements RewriteTest {
    /**
     * This method defines recipes used for testing.
     * @param spec stores settings for testing environment.
     */
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipes(
                new com.azure.recipes.v2recipes.DeleteMethod("com.boo.MyClass myMethod(..)", true)
                );
    }

    /**
     * Test delete method declaration with any parameters
     */
    @Test
    void testDeleteMethodDeclaration() {
        //new com.azure.recipes.v2recipes.DeleteMethod("com.example.MyClass myMethod(..)", true);
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public int myMethod() { return 1; }\n" +
                "    public void myMethod(String s) {}\n" +
                "    public void myMethod(Integer i) {}\n" +
                "    public void myMethod(Double d) {}\n" +
                "}\n";

        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "}\n";
        rewriteRun(java(before, after));
    }
    /**
     * Test delete invocation of method
     */

}
