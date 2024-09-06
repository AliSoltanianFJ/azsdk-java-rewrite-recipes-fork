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
     * Test method: asserts that call in declaring class removed.
     */
    @Test
    void testRemoveMethodCall_internal() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    private void myMethod() {}\n" +
                "    public void myMethodOutside() { myMethod(); }\n" +
                "}\n";

        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    private void myMethod() {}\n" +
                "    public void myMethodOutside() { }\n" +
                "}\n";

        rewriteRun(java(before, after));
    }

    /**
     * Test method: asserts that internal static method call removed.
     */
    @Test
    void testRemoveMethodCall_internalStatic() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public static int myMethod() { return 1; }\n" +
                "    public static void myMethodOutside() { int a = MyClass.myMethod(); }\n" +
                "}\n";
        /* comment */
        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public static int myMethod() { return 1; }\n" +
                "    public static void myMethodOutside() { int a; }\n" +
                "}\n";
        rewriteRun(java(before, after));
    }

    /**
     * Test method: asserts that instantiated method call is removed,
     * where used at variable declaration.
     */
    @Test
    void testRemoveMethodCall_instance() {
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
     * at variable assignment.
     */
    @Test
    void testRemoveMethodCall_asVarAssignment() {
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

    /**
     * Test method: asserts static method call removed in external class.
     */
    @Test
    void testRemoveMethodCall_staticInstance() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public static int myMethod() { return 1; }\n" +
                "}\n"  +
                "public class MyClass2 {\n" +
                "    public void myMethodOutside() { int a = MyClass.myMethod(); }\n" +
                "}\n" ;


        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public static int myMethod() { return 1; }\n" +
                "}\n"  +
                "public class MyClass2 {\n" +
                "    public void myMethodOutside() { int a; }\n" +
                "}\n" ;
        rewriteRun(java(before, after));
    }

    /**
     * Test method: asserts that method call is removed,
     * where used in child class.
     */
    @Test
    void testRemoveMethodCall_inherited() {
        @Language("java")String before = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public MyClass() { }" +
                "    public int myMethod() { return 1; }\n" +
                "}\n"  +
                "public class MyClass2 extends MyClass {\n" +
                "    public void myMethodOutside() { int a = super.myMethod(); }\n" +
                "}\n" ;


        @Language("java")String after = "package com.boo;\n" +
                "public class MyClass {\n" +
                "    public MyClass() { }" +
                "    public int myMethod() { return 1; }\n" +
                "}\n"  +
                "public class MyClass2 extends MyClass {\n" +
                "    public void myMethodOutside() { int a; }\n" +
                "}\n" ;
        rewriteRun(java(before, after));
    }

    /**
     * Test method: asserts that method call is removed,
     * where it is an interface method.
     */
    @Test
    void testRemoveMethodCall_interface() {
        @Language("java")String before = "package com.boo;\n" +
                "public interface MyClass {\n" +
                "    int myMethod();\n" +
                "}\n"  +
                "public class MyClass2 implements MyClass {\n" +
                "    @Override" +
                "    public int myMethod() { return 1; }\n" +
                "    public void myMethodOutside() { int a = myMethod(); }\n" +
                "}\n" ;


        @Language("java")String after = "package com.boo;\n" +
                "public interface MyClass {\n"+
                "    int myMethod();\n" +
                "}\n"  +
                "public class MyClass2 implements MyClass {\n" +
                "    @Override" +
                "    public int myMethod() { return 1; }\n" +
                "    public void myMethodOutside() { int a; }\n" +
                "}\n" ;
        rewriteRun(java(before, after));
    }

}
