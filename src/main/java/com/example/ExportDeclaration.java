package com.example;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;

public class ExportDeclaration {
    public static PsiElement create(Project project, String componentName) {
        PsiFileFactory factory = PsiFileFactory.getInstance(project);
        String content = "export { default as " + componentName + " } from './" + componentName + "'";
        PsiElement element = factory.createFileFromText(Language.findLanguageByID("JavaScript"), content);

        return element.getFirstChild();
    }
}
