package com.azure.recipes.v2recipes;

import org.jetbrains.annotations.NotNull;
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

public class DeleteMethod extends Recipe {

    /**
     * Methods to return name and description of recipe.
     */
    @Override
    public @NlsRewrite.DisplayName @NotNull String getDisplayName() {
        return "Delete method unfinished";
    }

    @Override
    public @NlsRewrite.Description @NotNull String getDescription() {
        return "Deletes a method without proper safety checks.";
    }

    /**
     * Methods to collect recipe parameters defined in recipe declaration.
     */
/*
    @Option(displayName = "Method pattern",
    description = "A method pattern that is used to find matching method declarations/invocations.",
    example = "com.example.Foo bar(..)")
    String methodPattern;

    @Option(displayName = "Match on overrides",
            description = "When enabled, find methods that are overrides of the method pattern.",
            required = false)
    @Nullable
    Boolean matchOverrides;
*/

    @Override
    public @NotNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return new DeleteMethodVisitor();
    }

    /**
     * The visitor used to apply the recipe.
     */
    private static class DeleteMethodVisitor extends JavaIsoVisitor<ExecutionContext> {

        /* Temporary hardcoded values */
        static String methodPattern = "com.azure.core.client.traits.HttpTrait clientOptions(..)";
        static Boolean matchOverrides = true;

        /**
         * Method matcher to identify method to delete.
         */
        private static final MethodMatcher methodMatcher = new MethodMatcher(methodPattern, matchOverrides);

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
        public J.@NotNull ClassDeclaration visitClassDeclaration(J.@NotNull ClassDeclaration classDeclaration, @NotNull ExecutionContext executionContext) {
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
    }
}

