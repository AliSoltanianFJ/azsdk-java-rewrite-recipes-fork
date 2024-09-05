package com.azure.recipes.v2recipes;

import org.openrewrite.*;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestInsertMethod extends Recipe {
    @Override
    public @NlsRewrite.DisplayName String getDisplayName() {
        return "Put Method";
    }

    @Override
    public @NlsRewrite.Description String getDescription() {
        return "put new Method.";
    }

    String code = "public String hello() { return \\\\\\\"Hello from #{}!\\\\\\\"; }";

    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {

        return new AddMethodDeclarationVisitor();
    }

    public class AddMethodDeclarationVisitor extends JavaIsoVisitor<ExecutionContext> {


        private final JavaTemplate methodTemplate = JavaTemplate.builder(code).build();

        @Override
        public J.@NonNull ClassDeclaration visitClassDeclaration(J.@NonNull ClassDeclaration classDeclaration, @NonNull ExecutionContext executionContext) {
            J.ClassDeclaration cd = super.visitClassDeclaration(classDeclaration, executionContext);

            if (cd.getType() == null) return cd;



                //Add Method to end of method body
            try {
                return cd.withBody(methodTemplate.apply(new Cursor(getCursor(),cd.getBody()),
                        cd.getBody().getCoordinates().lastStatement()));
            } catch (Exception e) {
                return cd;
            }
            //}
            //return cd;
        }
    }
}
