import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

/**
 * An (interface) HttpTrait migration test.
 * From: com.azure.core.client.traits.HttpTrait
 * To: io.clientcore.core.models.traits.HttpTrait

 * Testing simple method renaming.
 * Recipes:
 *   - org.openrewrite.java.ChangeMethodName:
 *       methodPattern: com.azure.core.client.traits.HttpTrait retryOptions(..)
 *       newMethodName: httpRetryOptions
 *       matchOverrides: true
 *   - org.openrewrite.java.ChangeMethodName:
 *       methodPattern: com.azure.core.client.traits.HttpTrait pipeline(..)
 *       newMethodName: httpPipeline
 *       matchOverrides: true
 *   - org.openrewrite.java.ChangeMethodName:
 *       methodPattern: com.azure.core.client.traits.HttpTrait addPolicy(..)
 *       newMethodName: addHttpPipelinePolicy
 *       matchOverrides: true
 *
 * @author Annabelle Mittendorf Smith
 */

public class HttpTraitRenameMethodsTest implements RewriteTest {
    /**
     * This method defines recipes used for testing.
     * @param spec stores settings for testing environment.
     */
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipeFromResource("/META-INF/rewrite/rewrite.yml",
                "com.azure.rewrite.java.core.MigrateAzureCoreSamplesToAzureCoreV2");
    }

    /**
     * Test that non-interface methods with the same name are not altered.
     */
    @Test
    void testDoesNotChangeNonInheritedMethods() {
        @Language("java")String noChange = "public class TestClass {\n" +
                "    public void retryOptions() {\n" +
                "    }\n" +
                "    public void pipeline() {\n" +
                "    }\n" +
                "    public void addPolicy() {\n" +
                "    }\n" +
                "}\n";
        rewriteRun(java(noChange));
    }

    /**
     * Test target methods are renamed.
     */
    @Test
    void declarativeRenameMethodsSuccessful() {
        @Language("java") String before = "import com.azure.core.client.traits.HttpTrait;\n" +
                "import com.azure.core.http.HttpPipeline;\n" +
                "import com.azure.core.http.policy.HttpPipelinePolicy;\n" +
                "import com.azure.core.http.policy.RetryOptions;\n" +
                "\n" +
                "public class BlankHttpTrait implements HttpTrait<BlankHttpTrait> {\n" +
                "    @Override\n" +
                "    public BlankHttpTrait pipeline(HttpPipeline pipeline) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public BlankHttpTrait addPolicy(HttpPipelinePolicy pipelinePolicy) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public BlankHttpTrait retryOptions(RetryOptions retryOptions) {\n" +
                "        return null;\n" +
                "    }\n" +
                "}";

        @Language("java") String after = "import com.azure.core.client.traits.HttpTrait;\n" +
                "import com.azure.core.http.HttpPipeline;\n" +
                "import com.azure.core.http.policy.HttpPipelinePolicy;\n" +
                "import com.azure.core.http.policy.RetryOptions;\n" +
                "\n" +
                "public class BlankHttpTrait implements HttpTrait<BlankHttpTrait> {\n" +
                "    @Override\n" +
                "    public BlankHttpTrait httpPipeline(HttpPipeline pipeline) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public BlankHttpTrait addHttpPipelinePolicy(HttpPipelinePolicy pipelinePolicy) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public BlankHttpTrait httpRetryOptions(RetryOptions retryOptions) {\n" +
                "        return null;\n" +
                "    }\n" +
                "}";

        rewriteRun(
                java(before,after)
        );
    }

}