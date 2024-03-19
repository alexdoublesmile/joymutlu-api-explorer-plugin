package com.joymutlu.apiexplorer;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.joymutlu.apiexplorer.config.PluginConfig;
import com.joymutlu.apiexplorer.exception.*;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.service.ApiScanService;
import com.joymutlu.apiexplorer.service.CodeGenerationService;
import com.joymutlu.apiexplorer.service.UserInputService;
import com.joymutlu.apiexplorer.strategy.classfind.ClassFindStrategyFactory;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.intellij.openapi.ui.Messages.*;

public class ExploreClassAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("-------- API Exploring triggered --------");
        final PluginConfig config = buildDefaultConfig();

        final Project project = e.getRequiredData(PROJECT);
        final Editor editor = e.getRequiredData(EDITOR);
        final UserInputService userInputService = new UserInputService(editor);
        final ApiScanService apiScanService = new ApiScanService(config);
        final CodeGenerationService codeGenerationService = new CodeGenerationService(config);

        try {
            final UserInput userInput = userInputService.getUserInput();

            final PsiClass exploredClass = ClassFindStrategyFactory.getStrategy(userInput.getType())
                    .findClass(userInput, editor.getDocument().getText(), project);
            System.out.printf("Found target class [%s]%n", exploredClass);

            final String generatedStr = codeGenerationService.generateApiString(
                    userInput,
                    exploredClass,
                    apiScanService.findApi(userInput, exploredClass));

            runWriteCommandAction(project, () -> updateEditor(userInput, generatedStr, editor.getDocument()));

        } catch (NoInitializingLineException | UnknownInputException ex) {
            showMessageDialog(project, ex.getMessage(), "Error", getErrorIcon());
        } catch (NoApiException | PrimitiveTypeException | NoMethodException | NoImportException | GenericSearchException ex) {
            showMessageDialog(project, ex.getMessage(), "Info", getInformationIcon());
        }
    }

    private void updateEditor(UserInput userInput, String generatedCode, @NotNull Document document) {
        if (!generatedCode.isEmpty()) {
            document.replaceString(userInput.getStartPosition(), userInput.getEndPosition(), generatedCode);
        }
    }

    @Override
    public void update(final AnActionEvent e) {
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(project != null && editor != null);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    private PluginConfig buildDefaultConfig() {
        return PluginConfig.builder()
                .withDeprecated(true)
                .withArgumentsAndReturns(false, false)
                .withParentApi(true, false)
                .withNaturalSorting(false)
                .build();
    }
}

// TODO: 27.02.2024 Generate API tree(for any depth without recursion)
// TODO: 04.03.2024 Add menu with configuration:
// - shortcut for gen strategy (unique, args, args + vars)
// - sorting by name & group, by name only, by class order, by args count & type, by return value...
// - filtering (deprecated, Object, customize exclusion by pattern, exclude parent, separate parent, include static vars)
// - tree generation depth
// TODO: 01.03.2024 Generate API for one of repeatable names in file
// TODO: 01.03.2024 Generate API for static class
// TODO: 01.03.2024 Generate API for static field
// TODO: 01.03.2024 Generate API in lambda
