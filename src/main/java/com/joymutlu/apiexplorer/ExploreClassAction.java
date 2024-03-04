package com.joymutlu.apiexplorer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.joymutlu.apiexplorer.exception.*;
import com.joymutlu.apiexplorer.model.ExploreConfig;
import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.service.ClassLoadService;
import com.joymutlu.apiexplorer.service.CodeGenerationService;
import com.joymutlu.apiexplorer.service.EditorService;

import java.util.List;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.intellij.openapi.ui.Messages.*;

public class ExploreClassAction extends AnAction {
    private final CodeGenerationService codeGenerationService = new CodeGenerationService();
    private final ClassLoadService classLoadService = new ClassLoadService();
    private EditorService editorService;
    private ExploreContext exploreCtx;
    private Project project;

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("-------- API Exploring triggered --------");
        exploreCtx = buildExploreContext(e);

        project = e.getRequiredData(PROJECT);
        editorService = new EditorService(e.getRequiredData(EDITOR));

        try {
            exploreCtx.setUserInput(editorService.defineUserInput());
            exploreCtx.setIndent(editorService.getLineOffset() - exploreCtx.getUserInput().getCaretOffset());
            System.out.printf("User input [%s] was defined as [%s] with indent size=[%d]%n",
                    exploreCtx.getUserInput(), exploreCtx.getInputType().name(), exploreCtx.getIndent().length());

            System.out.println("Scanning imports...");
            final List<String> importList = editorService.getImportList();
            System.out.printf("Imports: %s%n", importList);
            final String className = editorService.defineClassName(exploreCtx);
            System.out.printf("Necessary Class name: [%s]%n", className);

            final Class<?> exploreClass = classLoadService.loadClass(importList, className);
            System.out.printf("Loaded Class: [%s]%n", exploreClass);
            exploreCtx.setApi(exploreClass);
            final String generatedStr = codeGenerationService.generateApiString(exploreCtx);

            runWriteCommandAction(project, () -> updateEditor(generatedStr, exploreCtx));

        } catch (UnknownInputException | NoImportException | NoClassException ex) {
            showMessageDialog(project, ex.getMessage(), "Error", getErrorIcon());
        } catch (NoInitializingLineException | NoApiException ex) {
            showMessageDialog(project, ex.getMessage(), "Info", getInformationIcon());
        }
    }

    private ExploreContext buildExploreContext(AnActionEvent e) {
        return new ExploreContext(ExploreConfig.builder()
                .withDeprecated(true)
                .withArgumentsAndReturns(false, false)
                .withParentApi(true, false)
                .withNaturalSorting(false)
                .build());
    }

    private void updateEditor(String generatedCode, ExploreContext ctx) {
        if (!generatedCode.isBlank()) {
            editorService.getDocument().replaceString(
                    ctx.getUserInput().getStartPosition(),
                    ctx.getUserInput().getEndPosition(),
                    generatedCode);
        }
    }
}

// TODO: 04.03.2024 Add filtration by containsSymbol
// TODO: 01.03.2024 Generate API for one of repeatable names in file
// TODO: 01.03.2024 Generate API for static class
// TODO: 01.03.2024 Generate API for static field
// TODO: 01.03.2024 Generate API for method call
// TODO: 27.02.2024 Generate API tree(for any depth)
// TODO: 01.03.2024 Generate API in lambda
