package com.azure.recipes.v2recipes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.MethodCall;

/**
 * Recipe to find and delete all calls to a given method.
 * Probably requires more extensive testing.
 * Does not delete method Declarations or other objects outside the method body.
 * e.g. Integer a = sum(5,3); a = sum(a,b); becomes: Integer a; a;
 * Does not check, or account for syntactical effects of the deletion.
 * e.g.
 * @author Annabelle Mittendorf Smith
 */

@Value
@EqualsAndHashCode(callSuper = false)
public class RemoveMethodCall extends Recipe {

    /**
     * Configuration options required by the recipe.
     */
    @Option(displayName = "Method pattern",
            description = "A method pattern used to find matching method calls.",
            example = "*..* hello(..)")
    String methodPattern;


    @Override
    public  @NlsRewrite.DisplayName @NotNull String getDisplayName() {
        return "Remove methods calls";
    }

    @Override
    public @NlsRewrite.Description @NotNull String getDescription() {
        return "Checks for a method patterns and removes the method call from the class.";
    }

    /**
     * Json creator to allow the recipe to be used from a yaml file.
     *  All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in tests.
     */

    @JsonCreator
    public RemoveMethodCall(@NotNull @JsonProperty("methodPattern") String methodPattern) {
        this.methodPattern = methodPattern;
    }

    /**
     * Outer visitor method that returns the specific visitors specified
     * by the recipe.
     *
     * @return TreeVisitor
     */
    @Override
    public @NotNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return new RemoveMethodCallVisitor(new MethodMatcher(methodPattern, true));
    }

    @AllArgsConstructor
    public static class RemoveMethodCallVisitor extends JavaIsoVisitor<ExecutionContext> {
        /**
         * The MethodCall to match to be removed.
         */
        private final MethodMatcher methodMatcher;

        /**
         * The Suppliers that traverse the LST return real types of method calls.
         * Changes are handled in visitMethodCall.
         */
        @SuppressWarnings("NullableProblems")
        @Override
        public J.@Nullable NewClass visitNewClass(J.NewClass newClass, ExecutionContext ctx) {
            return visitMethodCall(newClass, super.visitNewClass(newClass, ctx));
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public J.@Nullable MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            return visitMethodCall(method, super.visitMethodInvocation(method, ctx));
        }


        /**
         * Method that performs the changes on the method calls found by the supplier visitors.
         * A MethodCall can be either a J. MethodInvocation or a J. MemberReference or a J. NewClass.
         */
        private <M extends MethodCall> @Nullable M visitMethodCall(M methodCall, M visitorSuper) {
            if (!methodMatcher.matches(methodCall)) {
                // Return un-altered if not a match
                return visitorSuper;
            }

            // Maybe remove import from declaring class
            if (methodCall.getMethodType() != null) {
                maybeRemoveImport(methodCall.getMethodType().getDeclaringType());
            }
            // Return null to delete if all checks pass.
            // @Nullable annotation required to return null.
            return null;
        }
    }
}
