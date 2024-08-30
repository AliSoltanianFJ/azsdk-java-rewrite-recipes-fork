package com.azure.recipes.v2recipes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.internal.lang.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.*;

/**
 * Recipe to find and delete a method.
 * In current version:
 * - Method pattern hard coded as: "com.azure.core.client.traits.HttpTrait clientOptions(..)"
 * TODO: Refactor to parameterised recipe for any method deletion.
 * @author Annabelle Mittendorf Smith
 */

@Value
@EqualsAndHashCode(callSuper = true)
public class DeleteMethod extends Recipe {

    /**
     * Collect recipe parameters.
     */
    @Option(displayName = "Method pattern",
            description = "A method pattern that is used to find matching method declarations/invocations.",
            example = "com.example.Foo bar(..)")
    @NonNull
    String methodPattern;

    @Option(displayName = "Match on overrides",
            description = "When enabled, find methods that are overrides of the method pattern.",
            required = false)
    @Nullable
    Boolean matchOverrides;

    // All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in your tests.
    @JsonCreator
    public DeleteMethod(@NonNull @JsonProperty("methodPattern") String methodPattern,
                        @Nullable @JsonProperty("matchOverrides") Boolean matchOverrides) {
        this.methodPattern = methodPattern;
        if (matchOverrides != null) this.matchOverrides = matchOverrides;
        else this.matchOverrides = Boolean.TRUE;
    }

    /**
     * Return recipe name and description.
     */
    @Override
    public @NlsRewrite.DisplayName @NonNull String getDisplayName() {
        return "Delete method unfinished";
    }

    @Override
    public @NlsRewrite.Description @NonNull String getDescription() {
        return "Deletes a method without proper safety checks.";
    }




    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        MethodMatcher methodMatcher = new MethodMatcher(methodPattern, true);

        return new JavaIsoVisitor<ExecutionContext>() {
            /**
             * Method that applies the recipe logic.
             * Visits each class declaration and returns a copy (more-or-less) leaving out
             * the method to be deleted.
             * @return A visitor without the stated method.

             * Recipe is adapted from:
             * org.openrewrite.staticanalysis.NoFinalizer
             * TODO: Maybe, include method uses
             * TODO: Maybe, use pre-screening
             */

            @Override
            public J.@NonNull ClassDeclaration visitClassDeclaration(J.@NonNull ClassDeclaration classDeclaration, @NonNull ExecutionContext executionContext) {
                J.ClassDeclaration cd = super.visitClassDeclaration(classDeclaration, executionContext);
                cd = cd.withBody(cd.getBody().withStatements(ListUtils.map(
                        cd.getBody().getStatements(), statement -> {
                            if (statement instanceof J.MethodDeclaration) {
                                if (methodMatcher.matches((J.MethodDeclaration) statement, classDeclaration)) {
                                    return null;
                                }
                            }
                            return statement;
                        }
                )));
                return cd;
            }
        };
    }
}