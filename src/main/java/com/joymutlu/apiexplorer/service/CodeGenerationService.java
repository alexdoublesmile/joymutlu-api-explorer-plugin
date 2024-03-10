package com.joymutlu.apiexplorer.service;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.config.PluginConfig;
import com.joymutlu.apiexplorer.exception.NoApiException;
import com.joymutlu.apiexplorer.model.ApiViewType;
import com.joymutlu.apiexplorer.model.InputType;
import com.joymutlu.apiexplorer.model.MethodDeclaration;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.strategy.CodeGenerationStrategy;
import com.joymutlu.apiexplorer.util.SortingUtils;

import java.util.Map;

import static java.lang.String.format;

public final class CodeGenerationService {
    private final Map<ApiViewType, CodeGenerationStrategy> strategyMap;

    private final UserInputService userInputService;
    private final PluginConfig config;

    public CodeGenerationService(UserInputService userInputService, PluginConfig config) {
        this.userInputService = userInputService;
        this.config = config;
        this.strategyMap = ApiViewType.getGenerationStrategyMap();
    }

    public String generateApiString(PsiClass psiClass, Map<MethodDeclaration, PsiMethod> api) throws NoApiException {
        final UserInput userInput = userInputService.getUserInput();
        System.out.printf("Generating API from %d methods...%n", api.size());
        if (api.isEmpty()) {
            throw new NoApiException(format("No %sAPI for %s", userInput.getType() == InputType.TYPE ? "static " : "", psiClass.getName()));
        }
        return api.values()
                .stream()
                .filter(method -> method.getName().startsWith(userInput.getFilter()))
                .sorted(SortingUtils.getSorting(config.getSortingType()))
                .map(method -> strategyMap.get(config.getApiViewType())
                        .generateApiLine(userInput, method))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
