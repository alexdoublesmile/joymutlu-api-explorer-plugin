package com.joymutlu.apiexplorer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

public class ExploreClassAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        final CaretModel caretModel = editor.getCaretModel();
        final int caretOffset = caretModel.getOffset();

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(caretOffset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);

        if (psiClass != null) {
            String generatedCode = generateMethodCalls(psiClass);
            editor.getDocument().replaceString(caretOffset - ".explore".length(), caretOffset, generatedCode);
        }
    }

    private String generateMethodCalls(PsiClass psiClass) {
        StringBuilder sb = new StringBuilder();
        for (PsiMethod method : psiClass.getAllMethods()) {
            sb.append(method.getName())
                    .append("(")
                    .append(");\n");
        }
        return sb.toString();
    }
}

// TODO: 27.02.2024  Handle potential errors (e.g., invalid PSI elements)
// TODO: 27.02.2024  Provide options for customizing default parameters
// TODO: 27.02.2024  Provide options for method filtering
// TODO: 27.02.2024  Test the plugin thoroughly in different scenarios
// TODO: 27.02.2024  Consider using a templating library for more complex code generation. Register the action with a custom live template
