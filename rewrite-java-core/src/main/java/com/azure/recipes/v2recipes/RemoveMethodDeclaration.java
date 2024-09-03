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
 * Recipe to find and delete a method Declaration.
 * ONLY deletes method declarations.
 * @author Annabelle Mittendorf Smith
 */

@Value
@EqualsAndHashCode(callSuper = false)
public class RemoveMethodDeclaration extends Recipe {


    @Option(displayName = "Method pattern",
            description = "A method pattern used to find matching method declaration.",
            example = "*..* hello(..)")
    @NonNull
    String methodPattern;

    @Option(displayName = "Match on overrides",
            description = "When enabled, find methods that are overrides of the method pattern.",
            required = false)
    @Nullable
    Boolean matchOverrides;

    @Override
    public @NlsRewrite.DisplayName @NonNull String getDisplayName() {
        return "Remove method declarations";
    }

    @Override
    public @NlsRewrite.Description @NonNull String getDescription() {
        return "Un-safe deletes method declaration matching `methodPattern`.";
    }

    /**
     * All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in your tests.
     * Json creator allows your recipes to be used from a yaml file.
     */
    @JsonCreator
    public RemoveMethodDeclaration(@NonNull @JsonProperty("methodPattern") String methodPattern,
                                   @Nullable @JsonProperty("matchOverrides") Boolean matchOverrides) {
        this.methodPattern = methodPattern;
        this.matchOverrides = matchOverrides;
    }

    /**
     * getVisitor
     *
     * @return TreeVisitor
     */
    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        MethodMatcher methodMatcher = new MethodMatcher(methodPattern, matchOverrides);

        return new JavaIsoVisitor<ExecutionContext>() {

            @Override
            public J.@NonNull ClassDeclaration visitClassDeclaration(J.@NonNull ClassDeclaration classDeclaration, @NonNull ExecutionContext executionContext) {
                J.ClassDeclaration cd = super.visitClassDeclaration(classDeclaration, executionContext);
                cd = cd.withBody(cd.getBody().withStatements(ListUtils.map(
                        cd.getBody().getStatements(), statement -> {
                            if (statement instanceof J.MethodDeclaration) {
                                if (methodMatcher.matches(((J.MethodDeclaration) statement).getMethodType())){
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