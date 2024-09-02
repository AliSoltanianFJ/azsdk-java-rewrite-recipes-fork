package com.azure.recipes.v2recipes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openrewrite.*;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.MethodCall;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * Recipe to find and delete all calls to a method.
 * Does not delete Declaration.
 * @author Annabelle Mittendorf Smith
 */

@Value
@EqualsAndHashCode(callSuper = false)
public class RemoveMethodCall extends Recipe {

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
     * All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in your tests.
     * Json creator allows your recipes to be used from a yaml file.
     */

    @JsonCreator
    public RemoveMethodCall(@NonNull @JsonProperty("methodPattern") String methodPattern) {
        this.methodPattern = methodPattern;
    }

    /**
     * getVisitor
     *
     * @return TreeVisitor
     */
    @Override
    public @NotNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return new RemoveMethodCallVisitor<>(new MethodMatcher(methodPattern,true), (n, it) -> true);
    }

    @AllArgsConstructor
    public static class RemoveMethodCallVisitor<P> extends JavaIsoVisitor<P> {
        /**
         * The MethodCall to match to be removed.
         */
        private final MethodMatcher methodMatcher;
        /**
         * All arguments must match the predicate for the MethodCall to be removed.
         */
        private final BiPredicate<Integer, Expression> argumentPredicate;

        /**
         * The Suppliers that traverse the LST and find calls to be removed.
         */
        @SuppressWarnings("NullableProblems")
        @Override
        public J.@Nullable NewClass visitNewClass(J.NewClass newClass, P p) {
            return visitMethodCall(newClass, () -> super.visitNewClass(newClass, p));
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public J.@Nullable MethodInvocation visitMethodInvocation(J.MethodInvocation method, P p) {
            return visitMethodCall(method, () -> super.visitMethodInvocation(method, p)); // 1, 3
        }

        /**
         * Performs the Recipe Logic.
         */
        // A MethodCall can be either a J. MethodInvocation or a J. MemberReference or a J. NewClass.
        private <M extends MethodCall> @Nullable M visitMethodCall(M methodCall, Supplier<M> visitSuper) {
            if (!methodMatcher.matches(methodCall)) {
                // Return un-altered
                return visitSuper.get();
            }

            // Extra checks here
            for (int i = 0; i < methodCall.getArguments().size(); i++) {
                if (!argumentPredicate.test(i, methodCall.getArguments().get(i))) {
                    return visitSuper.get();
                }
            }

            if (methodCall.getMethodType() != null) {
                maybeRemoveImport(methodCall.getMethodType().getDeclaringType());
            }
            // Return null to delete if all checks pass
            return null;
        }
    }
}
