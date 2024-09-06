package com.azure.recipes.v2recipes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.openrewrite.*;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.*;


@Value
@EqualsAndHashCode(callSuper = false)
public class AddMissingInterfaceMethod extends Recipe {

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
    public AddMissingInterfaceMethod(@NonNull @JsonProperty("fullyQualifiedClassName") String fullyQualifiedClassName,
                                     @NonNull @JsonProperty("methodPattern") String methodPattern,
                                     @NonNull @JsonProperty("methodTemplate") String methodTemplate) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.methodPattern = methodPattern;
        this.methodTemplateString = methodTemplate;
    }

    @Override
    public @NlsRewrite.DisplayName @NotNull String getDisplayName() {
        return "Add Missing Interface Method";
    }

    @Override
    public @NlsRewrite.Description @NotNull String getDescription() {
        return "Add a the missing method to classes implementing the declared interface.";
    }

    @Override
    public @NotNull TreeVisitor<?, ExecutionContext> getVisitor() {
        // A pre-check using a search recipe
        return Preconditions.check(new UsesType<>(fullyQualifiedClassName, true),
                new AddMethodToImplementationVisitor());
    }

    public class AddMethodToImplementationVisitor extends JavaIsoVisitor<ExecutionContext> {

        private final MethodMatcher methodMatcher = new MethodMatcher(methodPattern, true);

        @Override
        public J.@NotNull ClassDeclaration visitClassDeclaration(J.@NotNull ClassDeclaration classDeclaration, ExecutionContext ctx) {
            J.ClassDeclaration cd = super.visitClassDeclaration(classDeclaration,ctx);


            /*
            public enum Kind { //implements J
                Class,
                Enum,
                Interface,
                Annotation,
                Record,
                Value
            }
             */
            // Only modify concrete classes.
            if (cd.hasModifier(J.Modifier.Type.Abstract) || cd.getKind() == J.ClassDeclaration.Kind.Type.Interface) {
                return cd;
            }

            // Only modify classes that implement the interface.
            if (!TypeUtils.isAssignableTo(fullyQualifiedClassName, cd.getType())) {
            //if (!cd.getType().isAssignableTo(fullyQualifiedClassName)) {
                return cd;
            }

            // Build the method template
            JavaTemplate methodTemplate = JavaTemplate.builder(methodTemplateString).build();

             /*
            Here I ran into too many difficulties casting the return type dynamically from the method
            template.
            I also had difficulty filtering out parameters of the interface.
            I also had difficulty with testing while the altered class is 'between' interfaces.
             */

            // Don't modify if matching method already exists.
            J.ClassDeclaration finalCd = cd;
            if (cd.getBody().getStatements().stream()
                    .filter(statement -> statement instanceof J.MethodDeclaration)
                    .map(J.MethodDeclaration.class::cast)
                    .anyMatch(methodDeclaration -> methodMatcher.matches(methodDeclaration, finalCd))) {
                return cd;
            }

            // String param = (J.ParameterizedType)cd.getImplements().get(0)).getTypeParameters().get(0).toString();
            //System.out.println((param);
            // Maybe try filtered for matches:
            //JavaType maybeReturn = cd.getType().getInterfaces().get(0).getMethods().get(0).getReturnType();

            // Finally add method at the end of the class and return
            cd = cd.withBody(methodTemplate.apply(
                    new Cursor(getCursor(), cd.getBody()),
                    cd.getBody().getCoordinates().lastStatement()));
                    // add the relevant substitution value to apply

            // Maybe add or remove imports

            return cd;
        }
    }
}
