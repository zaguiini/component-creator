package com.example;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class ExportDeclaration {
    public static PsiElement create(Project project, String componentName) {
        PsiFileFactory factory = PsiFileFactory.getInstance(project);
        return factory.createFileFromText(Language.findLanguageByID("JavaScript"), "export { default as " + componentName + " } from './" + componentName + "'").getFirstChild();
    }
}
