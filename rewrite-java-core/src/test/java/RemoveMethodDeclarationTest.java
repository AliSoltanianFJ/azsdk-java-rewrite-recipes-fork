import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

/**
 * Unit tests for Remove method declaration recipe.
 * TODO: add more tests for better coverage.
 * @author Annabelle Mittendorf Smith
 */

public class RemoveMethodDeclarationTest implements RewriteTest {
    /**
     * This method defines recipes used for testing.
     * @param spec stores settings for testing environment.
     */
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipes(
                new com.azure.recipes.v2recipes.RemoveMethodDeclaration(
                        "com.boo.MyClass myMethod(..)", true
                ),
                new com.azure.recipes.v2recipes.RemoveMethodDeclaration(
                        "com.boo.MyOuterClass.MyInnerClass myMethod(..)", true
                ));
    }

    /**
     * Test method: asserts that simple method declaration is removed.
     */
    @Test
    void testRemoveMethodDeclaration() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public int myMethod() {}\n" +
                "}\n";

        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "}\n";
        rewriteRun(java(before, after));
    }

    /**
     * Test method: asserts that method declaration belonging to an inner class is removed.
     */
    @Test
    void testRemoveMethodDeclaration_innerClass() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyOuterClass {\n" +
                "   private class MyInnerClass {\n" +
                "       public void myMethod() {}\n" +
                "   }\n" +
                "}\n";

        @Language("java")String after = "package com.boo;\n" +
                "public class MyOuterClass {\n" +
                "   private class MyInnerClass {\n" +
                "   }\n" +
                "}\n";
        rewriteRun(java(before, after));
    }

    /**
     * Test method: asserts that overridden method declaration is removed,
     * when overriding method is target.
     */
    @Test
    void testRemoveMethodDeclaration_overriding() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyParentClass {\n" +
                "   public int myMethod() {}\n" +
                "}\n" +
                "public class MyClass extends MyParentClass {\n" +
                "   @Override\n" +
                "   public int myMethod() { super.myMethod(); }\n" +
                "}\n";

        @Language("java")String after = "package com.boo;\n" +
                "public class MyParentClass {\n" +
                "   public int myMethod() {}\n" +
                "}\n" +
                "public class MyClass extends MyParentClass {\n" +
                "}\n";
        rewriteRun(java(before, after));
    }

    /**
     * Test method: asserts that overridden method declaration is removed,
     * when overridden base method is target.
     */
    @Test
    void testRemoveMethodDeclaration_overridden() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "   public int myMethod() {}\n" +
                "}\n" +
                "public class MyChildClass extends MyClass {\n" +
                "   @Override\n" +
                "   public int myMethod() { super.myMethod(); }\n" +
                "}\n";

        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "}\n" +
                "public class MyChildClass extends MyClass {\n" +
                "}\n";
        rewriteRun(java(before, after));
    }

}
