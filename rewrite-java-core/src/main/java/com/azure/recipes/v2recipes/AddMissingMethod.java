package com.azure.recipes.v2recipes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.openrewrite.*;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.TreeVisitingPrinter;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.*;

import java.util.Arrays;
import java.util.List;


@Value
@EqualsAndHashCode(callSuper = false)
public class AddMissingMethod extends Recipe {

    @Option(displayName = "Fully Qualified Class Name",
            description = "A fully qualified class being implemented with missing method.",
            example = "com.yourorg.FooBar")
    String fullyQualifiedClassName;

    @Option(displayName = "Method Pattern",
            description = "A method pattern for matching required method definition.",
            example = "*..* hello(..)")
    String methodPattern;

    @Option(displayName = "Method Template",
            description = "Template of method to add",
            example = "public String hello() { return \\\"Hello from #{}!\\\"; }")
    String methodTemplateString;

    /**
     * All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in your tests.
     * Json creator allows your recipes to be used from a yaml file.
     */
    @JsonCreator
    public AddMissingMethod(@NonNull @JsonProperty("fullyQualifiedClassName") String fullyQualifiedClassName,
                                @NonNull @JsonProperty("methodPattern") String methodPattern,
                                @NonNull @JsonProperty("methodTemplate") String methodTemplate) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.methodPattern = methodPattern;
        this.methodTemplateString = methodTemplate;
    }

    @Override
    public @NlsRewrite.DisplayName @NotNull String getDisplayName() {
        return "Add Missing Methods";
    }

    @Override
    public @NlsRewrite.Description @NotNull String getDescription() {
        return "Add a missing method from the declared interface.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(new UsesType<>(fullyQualifiedClassName, true), new ImplementationVisitor());
    }

    public class ImplementationVisitor extends JavaIsoVisitor<ExecutionContext> {


        private final MethodMatcher methodMatcher = new MethodMatcher(methodPattern, true);

        @Override
        public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDeclaration, ExecutionContext ctx) {
            J.ClassDeclaration cd = super.visitClassDeclaration(classDeclaration,ctx);

            // Only modify concrete classes.
            if (cd.hasModifier(J.Modifier.Type.Abstract) || cd.getKind() == J.ClassDeclaration.Kind.Type.Interface) {
                return cd;
            }

            // Only modify classes that implement the interface.
            if (!TypeUtils.isAssignableTo(fullyQualifiedClassName, cd.getType())) {
            //if (!cd.getType().isAssignableTo(fullyQualifiedClassName)) {
                return cd;
            }
            // TODO change to get interface params
           // String mt2 = methodTemplateString.replace("<T>", cd.getType().getFullyQualifiedName());
            //System.out.println(mt2);
            JavaTemplate methodTemplate = JavaTemplate.builder(methodTemplateString).build();
            // Don't modify if method already exists.
            J.ClassDeclaration finalCd = cd;
            if (cd.getBody().getStatements().stream()
                    .filter(statement -> statement instanceof J.MethodDeclaration)
                    .map(J.MethodDeclaration.class::cast)
                    .anyMatch(methodDeclaration -> methodMatcher.matches(methodDeclaration, finalCd))) {
                return cd;
            }

            // Finally add method at the end of the class and return
            //System.out.println(((J.ParameterizedType)cd.getImplements().get(0)).getTypeParameters().get(0).toString());
            cd = cd.withBody(methodTemplate.apply(
                    new Cursor(getCursor(), cd.getBody()),
                    cd.getBody().getCoordinates().lastStatement()));

                    //cd.getType()));
            //TODO: change so targets interface params
            //maybeAddImport("io.clientcore.core.http.models.HttpRedirectOptions");
            //System.out.println(TreeVisitingPrinter.printTree(getCursor()));
            return cd;
        }
    }
}
