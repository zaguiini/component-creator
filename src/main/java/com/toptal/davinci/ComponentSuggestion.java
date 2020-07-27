package com.toptal.davinci;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.lang.javascript.psi.JSXmlLiteralExpression;
import com.intellij.lang.javascript.psi.ecma6.impl.JSXXmlLiteralExpressionImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ComponentSuggestion extends PsiElementBaseIntentionAction implements IntentionAction {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Create component";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ComponentSuggestion";
    }

    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        try {
            return element.getContext().getReference().resolve() instanceof JSXmlLiteralExpression;
        } catch(AssertionError e) {
            return false;
        }
    }

    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        JSXXmlLiteralExpressionImpl component = (JSXXmlLiteralExpressionImpl) element.getContext();

        if(component == null) {
            return;
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
