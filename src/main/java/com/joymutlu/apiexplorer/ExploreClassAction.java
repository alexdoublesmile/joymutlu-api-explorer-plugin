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

        runWriteCommandAction(project, generateAPI());
    }

    private ExploreContext buildExploreContext(AnActionEvent e) {
        return new ExploreContext(ExploreConfig.builder()
                .withDeprecated(true)
                .withArgumentsAndReturns(false, false)
                .withParentApi(true, false)
                .build());
    }

    private Runnable generateAPI() {
        return () -> {
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
                updateEditor(generatedStr, exploreCtx);

            } catch (UnknownInputException | NoImportException | NoClassException e) {
                showMessageDialog(project, e.getMessage(), "Error", getErrorIcon());
            } catch (NoInitializingLineException | NoApiException e) {
                showMessageDialog(project, e.getMessage(), "Info", getInformationIcon());
            }
        };
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
