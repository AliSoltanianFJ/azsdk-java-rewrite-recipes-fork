import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

/**
 * Unit tests for Remove method call recipe.
 * TODO: add more tests for full test coverage.
 * @author Annabelle Mittendorf Smith
 */

public class RemoveMethodCallTest implements RewriteTest {

    /**
     * This method defines recipes used for testing.
     * @param spec stores settings for testing environment.
     */
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipes(
                new com.azure.recipes.v2recipes.RemoveMethodCall(
                        "com.boo.MyClass myMethod(..)")
        );
    }


    /**
     * Test method: asserts that non-target method with same name not removed.
     */
    @Test
    void testSameNameMethodsNotRemoved() {
        @Language("java")String noChange =
                "public class MyClass {\n" +
                "    public void myMethod() {}\n" +
                "    public void myMethodOutside() { myMethod(); }\n" +
                "}\n";

        rewriteRun(java(noChange));
    }

    /**
     * Test method: asserts that call to void method is removed.
     */
    @Test
    void testRemoveMethodCall_void() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public void myMethod() {}\n" +
                "    public void myMethodOutside() { myMethod(); }\n" +
                "}\n";

        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public void myMethod() {}\n" +
                "    public void myMethodOutside() { }\n" +
                "}\n";

        rewriteRun(java(before, after));
    }

    /**
     * Test method: asserts that method call is removed, where return is unused.
     */
    @Test
    void testRemoveMethodCall_unusedReturnInt() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public int myMethod() { return 1; }\n" +
                "    public void myMethodOutside() { myMethod(); }\n" +
                "}\n";
        /* comment */
        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public int myMethod() { return 1; }\n" +
                "    public void myMethodOutside() { }\n" +
                "}\n";
        rewriteRun(java(before, after));
    }

    /**
     * Test method: asserts that method call is removed, where used
     * by a variable declaration.
     */
    @Test
    void testRemoveMethodCall_atVarDeclaration() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public MyClass() { }" +
                "    public int myMethod() { return 1; }\n" +
                "}\n"  +
                "public class MyClass2 {\n" +
                "    public void myMethodOutside() { MyClass t = new MyClass();\n" +
                "    int a = t.myMethod(); }\n" +
                "}\n" ;


        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public MyClass() { }" +
                "    public int myMethod() { return 1; }\n" +
                "}\n"  +
                "public class MyClass2 {\n" +
                "    public void myMethodOutside() { MyClass t = new MyClass();\n" +
                "    int a; }\n" +
                "}\n" ;
        rewriteRun(java(before, after));
    }
    /**
     * Test method: asserts that method call is removed, where used
     * by a variable assignment.
     */
    @Test
    void testRemoveMethodCall_atVarAssignment() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public MyClass() { }" +
                "    public int myMethod() { return 1; }\n" +
                "}\n"  +
                "public class MyClass2 {\n" +
                "    public void myMethodOutside() { MyClass t = new MyClass();\n" +
                "    int a;\n" +
                "    a = t.myMethod(); }\n" +
                "}\n" ;


        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public MyClass() { }" +
                "    public int myMethod() { return 1; }\n" +
                "}\n"  +
                "public class MyClass2 {\n" +
                "    public void myMethodOutside() { MyClass t = new MyClass();\n" +
                "    int a;\n" +
                "    a; }\n" +
                "}\n" ;
        rewriteRun(java(before, after));
    }



}
