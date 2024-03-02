package com.joymutlu.apiexplorer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.joymutlu.apiexplorer.exception.*;
import com.joymutlu.apiexplorer.model.ExploreConfig;
import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.service.ClassSearchService;
import com.joymutlu.apiexplorer.service.CodeGenerationService;
import com.joymutlu.apiexplorer.util.ImportUtils;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.util.List;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.intellij.openapi.ui.Messages.*;
import static java.lang.String.format;

public class ExploreClassAction extends AnAction {
    private final CodeGenerationService codeGenerationService = new CodeGenerationService();
    private final ClassSearchService classSearchService = new ClassSearchService();
    private ExploreContext exploreCtx;
    private Project project;
    private Document document;
    private CharSequence editorText;
    private int caret;

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("-------- API Exploring triggered --------");
        exploreCtx = buildExploreContext(e);

        Editor editor = e.getRequiredData(EDITOR);
        project = e.getRequiredData(PROJECT);
        document = editor.getDocument();
        editorText = document.getText();
        caret = editor.getCaretModel().getOffset();

        runWriteCommandAction(project, generateAPI());
    }

    private ExploreContext buildExploreContext(AnActionEvent e) {
        return new ExploreContext(ExploreConfig.builder()
                .withDeprecated(true)
                .withArguments(false)
                .withReturnValues(false)
                .withParentApi(true, false)
                .build());
    }

    private Runnable generateAPI() {
        return () -> {
            try {
                System.out.println("Capturing line...");
                final int lineNumber = document.getLineNumber(caret);
                final int lineStartOffset = document.getLineStartOffset(lineNumber);
                final int lineEndOffset = document.getLineEndOffset(lineNumber);
                final String line = document.getText(new TextRange(lineStartOffset, lineEndOffset));
                System.out.printf("Captured line %d: [%s]%n", lineNumber, line);

                exploreCtx.setUserInput(defineUserInput(line, caret - lineStartOffset));
                exploreCtx.setIndent(StringUtils.calcSpaces(line, caret));
                System.out.printf("User input [%s] was defined as [%s] with indent size=[%d]%n",
                        exploreCtx.getUserInput(), exploreCtx.getInputType().name(), exploreCtx.getIndent().length());

                final List<String> importList = ImportUtils.getImportList(editorText);
                System.out.printf("Imports: %s%n", importList);
                final String className = classSearchService.findClassName(exploreCtx, editorText);
                System.out.printf("Necessary Class name: [%s]%n", className);

                final Class<?> exploreClass = classSearchService.findClass(importList, className);
                System.out.printf("Defined Class: [%s]%n", exploreClass);
                exploreCtx.setApi(exploreClass);
                final String generatedStr = codeGenerationService.generateApiString(exploreCtx);
                updateEditor(generatedStr, exploreCtx);

            } catch (UnknownInputException | NoImportException | NoClassException e) {
                showMessageDialog(project, e.getMessage(), "Error", getErrorIcon());
            } catch (NoInitializingLineException | NoApiException e) {
                showMessageDialog(project, e.getMessage(), "Info", getInformationIcon());
            }
        };
    }

    private String defineUserInput(String line, int caretPosition) {
        System.out.println("Defining user input...");
        String userInput = "";
        for (int i = caretPosition - 1; i >= 0; i--) {
            final char c = line.charAt(i);
            if (c == ' ') {
                break;
            }
            if (c != '.') {
                userInput = c + userInput;
            }
        }
        for (int i = caretPosition; i < line.length(); i++) {
            final char c = line.charAt(i);
            if (c == ' ') {
                break;
            }
            if (c != '.') {
                userInput = userInput + c;
            }
        }
//        final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
//        String userInput = "";
//        if (psiFile != null) {
//            userInput = findUserInput(psiFile, caret - 1).getText();
//        }
        return userInput;
    }

    private PsiElement findUserInput(PsiFile psiFile, int caret) {
        PsiElement elementAtCaret = psiFile.findElementAt(caret);
        if (elementAtCaret == null) {
            elementAtCaret = findUserInput(psiFile, caret - 1);
        }
        return elementAtCaret;
    }

    private void updateEditor(String generatedCode, ExploreContext ctx) {
        if (!generatedCode.isBlank()) {
            document.replaceString(caret - ctx.getUserInput().length() - 1, caret, generatedCode);
        }
    }
}

// TODO: 27.02.2024 Generate all methods with overload & some params
// TODO: 27.02.2024 Generate all methods with overload, params & return var
// TODO: 27.02.2024 Generate checkers(return boolean)
// TODO: 27.02.2024 Generate getters(startsWith "get..")
// TODO: 27.02.2024 Generate setters(startsWith "set..")
// TODO: 29.02.2024 add different sorting strategies for methods
// TODO: 27.02.2024 Provide options for method filtering
// TODO: 27.02.2024 Provide options for customizing default parameters
// TODO: 01.03.2024 Fix errors

// TODO: 01.03.2024 Generate API after method call
// TODO: 01.03.2024 Generate API after static/non-static direct field call
// TODO: 27.02.2024 Generate all method tree(depth) with default params
// TODO: 27.02.2024 Test the plugin thoroughly in different scenarios
