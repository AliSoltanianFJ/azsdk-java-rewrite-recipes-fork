package com.azure.recipes.v2recipes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.internal.lang.NonNull;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A recipe to add a new method declaration to a class.
 * If an identical method already exists no changes are made.
 * If the class cannot be found, no changes are made.
 * Does not delete Declaration.
 * @author Annabelle Mittendorf Smith
 */

@Value
@EqualsAndHashCode(callSuper = false)
public class AddMethodDeclaration extends Recipe {


    @Option(displayName = "Fully Qualified Class Name",
            description = "A fully qualified class name indicating which class to add the method to.",
            example = "com.example.FooBar")
    @NonNull
    String fullyQualifiedClassName;

    @Option(displayName = "Method Template",
            description = "A valid name for the method being created.",
            example = "helloWorld")
    @NonNull
    String methodTemplate;
/*
    @Option(displayName = "Arguments",
            description = "A comma separated list of arguments",
            example = "String string, Integer integer",
            required = false)
    String arguments;

    @Option(displayName = "ReturnType",
            description = "A comma separated list of arguments",
            example = "String string, Integer integer",
            required = false))

    @Option(displayName = "Access Modifier",
            description = "Add an access modifier",
            example = "private",
            required = false)
    String accessModifier;

    @Option(displayName = "Method Body Template",
            description = "A template for the body of the method.",
            example = "{ return \\\"Hello from #{}!\\\"; }",
            required = false)
    String methodBodyTemplate;

*/

    /**
     * All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in your tests.
     * Json creator allows your recipes to be used from a yaml file.
     */
    @JsonCreator
    public AddMethodDeclaration(@NonNull @JsonProperty("fullyQualifiedClassName") String fullyQualifiedClassName,
                                   @NonNull @JsonProperty("methodTemplate") String methodTemplate) {
                                   //@Nullable @JsonProperty("methodTemplate") String methodTemplate) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.methodTemplate = methodTemplate;
        //if (methodTemplate != null) { this.methodBodyTemplate = methodTemplate; }
       // else { this.methodBodyTemplate = "{}"; }

    }

    @Override
    public @NlsRewrite.DisplayName @NonNull String getDisplayName() {
        return "Add method declaration";
    }

    @Override
    public @NlsRewrite.Description @NonNull String getDescription() {
        return "Adds a method declaration to the target class.";
    }

    /**
     * Visitor method
     */

    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {

        return new AddMethodDeclarationVisitor();
    }

    public class AddMethodDeclarationVisitor extends JavaIsoVisitor<ExecutionContext> {

        private final Pattern pattern = Pattern.compile("\\w+\\((.*)\\)");
        private final Matcher matcher = pattern.matcher(AddMethodDeclaration.this.methodTemplate);
        private final String name = matcher.find() ? matcher.group() : null;

        MethodMatcher methodMatcher = new MethodMatcher(fullyQualifiedClassName + " " + name);

        private final JavaTemplate methodTemplate = JavaTemplate.builder(AddMethodDeclaration.this.methodTemplate).build();

        @Override
        public J.@NonNull ClassDeclaration visitClassDeclaration(J.@NonNull ClassDeclaration classDeclaration, @NonNull ExecutionContext executionContext) {
            J.ClassDeclaration cd = super.visitClassDeclaration(classDeclaration, executionContext);

            if (cd.getType() == null) return cd;

            if (cd.getType().getFullyQualifiedName().equals(fullyQualifiedClassName)) {

                // Check for pre-existing method declaration matching new method
                if (cd.getBody().getStatements().stream()
                        .filter(statement -> statement instanceof J.MethodDeclaration)
                        .map(J.MethodDeclaration.class::cast)
                        .anyMatch(methodDeclaration -> methodMatcher.matches(methodDeclaration,cd))) {
                    return cd;
                }

                //Add Method to end of method body
                try {
                    return cd.withBody(methodTemplate.apply(new Cursor(getCursor(),cd.getBody()),
                            cd.getBody().getCoordinates().lastStatement(),fullyQualifiedClassName ));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return cd;
                }

            }
            return cd;
        }
    }
}
