import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.java.ChangeType;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

/**
 * RequestOptionsTest is used to test out the recipe that converts com.azure.core.http.rest.RequestOptions
 * to io.clientcore.core.http.models.RequestOptions.
 * @author Ali Soltanian Fard Jahromi
 */
class RequestOptionsTest implements RewriteTest {

    /**
     * This method sets which recipe should be used for testing
     * @param spec stores settings for testing environment; e.g. which recipes to use for testing
     */
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ChangeType("com.azure.core.http.rest.RequestOptions",
                "io.clientcore.core.http.models.RequestOptions", null));
    }

    /**
     * This test method is used to make sure that the class type and import for RequestOptions is updated
     */
    @Test
    void testInit() {
        @Language("java") String before = "import com.azure.core.http.rest.RequestOptions;";
        before += "\npublic class Testing {";
        before += "\n  public Testing(){";
        before += "\n    com.azure.core.http.rest.RequestOptions r = new RequestOptions();";
        before += "\n  }";
        before += "\n}";

        @Language("java") String after = "import io.clientcore.core.http.models.RequestOptions;";
        after += "\n\npublic class Testing {";
        after += "\n  public Testing(){";
        after += "\n    io.clientcore.core.http.models.RequestOptions r = new RequestOptions();";
        after += "\n  }";
        after += "\n}";
        rewriteRun(
                java(before,after)
        );
    }
}
