package com.joymutlu.apiexplorer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ExploreClassAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        final CaretModel caretModel = editor.getCaretModel();
        final int caretOffset = caretModel.getOffset();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiFile currentPsiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            final int lastElementOffset = caretOffset - 1;
            PsiElement elementAtCaret = currentPsiFile.findElementAt(lastElementOffset);
            String userInput = elementAtCaret.getText();
            final Path currentFilePath = Path.of(elementAtCaret.getContainingFile().getVirtualFile().getCanonicalPath());
            String className = "";
            ;
            try (final Stream<String> lines = Files.lines(currentFilePath)) {
                className = lines
                        .filter(s -> s.startsWith("import"))
                        .map(s -> s.substring(7, s.length() - 1))
                        .filter(s -> s.endsWith(userInput))
                        .findFirst()
                        .orElse("");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println(className);

            if (!className.isBlank()) {
                final Class<?> clazz;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                // Generate code if target class found
                if (clazz != null) {
                    String generatedCode = generateMethodCalls(clazz);
                    editor.getDocument().replaceString(caretOffset - ".explore".length(), caretOffset, generatedCode);
                } else {
                    Messages.showMessageDialog(project, "Class '" + userInput + "' not found.", "Error", Messages.getErrorIcon());
                }
            }
//            PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        });
    }

    private String getPackageNameFromImport(PsiClass psiClass, String classname) {
        String packageName = "";
        System.out.println(psiClass.getText());
        return packageName;
    }

    private String generateMethodCalls(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        for (Method method : clazz.getDeclaredMethods()) {
            sb.append(method.getName())
                    .append("(")
                    .append(");\n");
        }
        return sb.toString();
    }
}

// TODO: 27.02.2024  Generate compile-safe code for static only
// TODO: 27.02.2024  Generate tabbed code
// TODO: 27.02.2024  Generate compile-safe code for virtual methods
// TODO: 27.02.2024  Generate checkers(return boolean)
// TODO: 27.02.2024  Generate getters(startsWith "get..")
// TODO: 27.02.2024  Generate setters(startsWith "set..")
// TODO: 27.02.2024  Generate all methods(incl.static,incl.parent)
// TODO: 27.02.2024  Generate all methods with overload & some params
// TODO: 27.02.2024  Generate all methods with overload, params & return var
// TODO: 27.02.2024  Generate all method tree(depth) with default params
// TODO: 27.02.2024  Handle potential errors (e.g., invalid PSI elements)
// TODO: 27.02.2024  Provide options for customizing default parameters
// TODO: 27.02.2024  Provide options for method filtering
// TODO: 27.02.2024  Test the plugin thoroughly in different scenarios
// TODO: 27.02.2024  Consider using a templating library for more complex code generation. Register the action with a custom live template
