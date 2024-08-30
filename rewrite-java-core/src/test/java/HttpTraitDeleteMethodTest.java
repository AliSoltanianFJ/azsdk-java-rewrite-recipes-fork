import com.azure.recipes.v2recipes.DeleteMethod;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

/**
 * An (interface) HttpTrait migration test.
 * From: com.azure.core.client.traits.HttpTrait
 * To: io.clientcore.core.models.traits.HttpTrait

 * Testing custom method deletion recipe DeleteMethod.
 * DeleteMethod current version notes:
 * - deletes "com.azure.core.client.traits.HttpTrait clientOptions(..)".
 * - matchOverrides = true.
 * - unsafe delete method.
 * - method pattern hardcoded.
 * @author Annabelle Mittendorf Smith
 */

public class HttpTraitDeleteMethodTest implements RewriteTest {
    /**
     * This method defines recipes used for testing.
     * @param spec stores settings for testing environment.
     */
    @Override
    public void defaults(RecipeSpec spec) {

        spec.recipe(new DeleteMethod());

    }

    /**
     * Test that non-interface methods with the same name are not deleted.
     */
    @Test
    void testDoesNotDeleteNonInheritedMethod() {
        @Language("java")String noChange = "public class TestClass {\n" +
                "    public void clientOptions() {\n" +
                "    }\n" +
                "}\n";
        rewriteRun(java(noChange));
    }

    /**
     * Test deletion of target method declaration ONLY.
     */

    @Test
    void DeleteMethodSuccessful() {
        @Language("java") String before = "import com.azure.core.client.traits.HttpTrait;\n" +
                "import com.azure.core.util.ClientOptions;\n" +
                "\n" +
                "public class BlankHttpTrait implements HttpTrait<BlankHttpTrait> {\n" +
                "    public ClientOptions clientOptions;\n" +
                "    @Override\n" +
                "    public BlankHttpTrait clientOptions(ClientOptions clientOptions) {\n" +
                "        return null;\n" +
                "    }\n" +
                "}";

        @Language("java") String after = "import com.azure.core.client.traits.HttpTrait;\n" +
                "import com.azure.core.util.ClientOptions;\n" +
                "\n" +
                "public class BlankHttpTrait implements HttpTrait<BlankHttpTrait> {\n" +
                "    public ClientOptions clientOptions;\n" +
                "}";

        rewriteRun(
                java(before,after)
        );
    }


}