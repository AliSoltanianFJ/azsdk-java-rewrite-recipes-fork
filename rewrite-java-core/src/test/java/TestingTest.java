import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.SourceSpec;

import static org.openrewrite.java.Assertions.java;

public class TestingTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
    }

    @Test
    void test() {
        @Language("java") String before = "package com.azure.recipes.v2recipes;\n" +
                "\n" +
                "\n" +
                "public class test2 extends TestClass {\n" +
                "\n" +
                "}";
        @Language("java") String after = "package com.azure.recipes.v2recipes;\n" +
                "\n" +
                "\n" +
                "public class test2 implements TestClass {\n" +
                "\n" +
                "}";
        rewriteRun(java(before,after));
    }


}
