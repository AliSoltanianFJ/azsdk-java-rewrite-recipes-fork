package com.azure.recipes.core.v2;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

/**
 * This test class is to check the coverage of our recipes using the samples in
 * azure-ai-translation-text-v1 and azure-ai-translation-text-v2.
 * The recipes are run over the copied contents of azure-ai-translation-text-v1
 * TextTranslationSample.java.
 * The contents of azure-ai-translation-text-v2 TextTranslationSample.java
 * are used as the expected outcome.
 * This test will pass if the transformed code from running the recipes EXACTLY matches
 * the provided v2 sample.
 *
 * NOTES:
 * To ensure these tests are accurate, update via pull from remote main before running.
 * This test requires changes to rewrite-java-core/pom.xml that causes a dependency conflict
 * with other test classes.
 * This class requires:
 *      <dependency>
 *            <groupId>com.azure</groupId>
 *            <artifactId>azure-ai-translation-text-v1</artifactId>
 *            <version>1.0.0-beta.1</version>
 *            <scope>test</scope>
 *      </dependency>
 *
 * TODO resolve dependency conflict if this branch is to be merged.
 * @author Annabelle Mittendorf Smith
 *
 */

public class TextTranslationSampleTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipeFromResource("/META-INF/rewrite/rewrite.yml",
                "com.azure.rewrite.java.core.MigrateAzureCoreSamplesToAzureCoreV2");
    }

    @Test
    void test() {
        @Language("java") String before = "package java.com.azure.ai.translation.text;\n" +
                "\n" +
                "import com.azure.ai.translation.text.TextTranslationClient;\n" +
                "import com.azure.ai.translation.text.TextTranslationClientBuilder;\n" +
                "import com.azure.ai.translation.text.models.InputTextItem;\n" +
                "import com.azure.ai.translation.text.models.TranslatedTextItem;\n" +
                "import com.azure.core.credential.KeyCredential;\n" +
                "import com.azure.core.http.policy.FixedDelayOptions;\n" +
                "import com.azure.core.http.policy.HttpLogDetailLevel;\n" +
                "import com.azure.core.http.policy.HttpLogOptions;\n" +
                "import com.azure.core.http.policy.RetryOptions;\n" +
                "\n" +
                "import java.time.Duration;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class TextTranslationSample {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        TextTranslationClient textTranslationClient = new TextTranslationClientBuilder()\n" +
                "                .credential(new KeyCredential(\"<api-key>\"))\n" +
                "                .endpoint(\"<endpoint>\")\n" +
                "                .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS))\n" +
                "                .retryOptions(new RetryOptions(new FixedDelayOptions(3, Duration.ofMillis(50))))\n" +
                "                .buildClient();\n" +
                "\n" +
                "        List<InputTextItem> inputTextItems = Arrays.asList(new InputTextItem(\"hello world\"));\n" +
                "        List<TranslatedTextItem> result = textTranslationClient.translate(Arrays.asList(\"es\"), inputTextItems);\n" +
                "\n" +
                "        result.stream()\n" +
                "                .flatMap(translatedTextItem -> translatedTextItem.getTranslations().stream())\n" +
                "                .forEach(translation -> System.out.println(\"Translated text to \" + translation.getTargetLanguage() + \" : \" + translation.getText()));\n" +
                "    }\n" +
                "}";

        @Language("java") String after = "package java.com.azure.ai.translation.text;\n" +
                "\n" +
                "import com.azure.ai.translation.text.TextTranslationClient;\n" +
                "import com.azure.ai.translation.text.TextTranslationClientBuilder;\n" +
                "import com.azure.ai.translation.text.models.InputTextItem;\n" +
                "import com.azure.ai.translation.text.models.TranslatedTextItem;\n" +
                "import io.clientcore.core.credential.KeyCredential;\n" +
                "import io.clientcore.core.http.models.HttpLogOptions;\n" +
                "import io.clientcore.core.http.models.HttpRetryOptions;\n" +
                "\n" +
                "import java.io.IOException;\n" +
                "import java.time.Duration;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class TextTranslationSample {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        TextTranslationClient textTranslationClient = new TextTranslationClientBuilder()\n" +
                "                .credential(new KeyCredential(\"<api-key>\"))\n" +
                "                .endpoint(\"<endpoint>\")\n" +
                "                .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogOptions.HttpLogDetailLevel.BODY_AND_HEADERS))\n" +
                "                .httpRetryOptions(new HttpRetryOptions(3, Duration.ofMillis(50)))\n" +
                "                .buildClient();\n" +
                "\n" +
                "        List<InputTextItem> inputTextItems = Arrays.asList(new InputTextItem(\"hello world\"));\n" +
                "        List<TranslatedTextItem> result = null;\n" +
                "        try {\n" +
                "            result = textTranslationClient.translate(Arrays.asList(\"es\"), inputTextItems);\n" +
                "        } catch (IOException e) {\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "\n" +
                "        result.stream()\n" +
                "                .flatMap(translatedTextItem -> translatedTextItem.getTranslations().stream())\n" +
                "                .forEach(translation -> System.out.println(\"Translated text to \" + translation.getTargetLanguage() + \" : \" + translation.getText()));\n" +
                "    }\n" +
                "}";
        rewriteRun(

//                spec -> spec.cycles(2)
//                        .expectedCyclesThatMakeChanges(2),
                java(before,after)
        );
    }
}
