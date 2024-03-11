package com.joymutlu.apiexplorer.service;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.config.PluginConfig;
import com.joymutlu.apiexplorer.exception.NoApiException;
import com.joymutlu.apiexplorer.model.ApiViewType;
import com.joymutlu.apiexplorer.model.InputType;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.strategy.codegeneration.CodeGenerationStrategy;
import com.joymutlu.apiexplorer.util.SortingUtils;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public final class CodeGenerationService {
    private final Map<ApiViewType, CodeGenerationStrategy> strategyMap;

    private final PluginConfig config;

    public CodeGenerationService(PluginConfig config) {
        this.config = config;
        this.strategyMap = ApiViewType.getGenerationStrategyMap();
    }

    public String generateApiString(UserInput userInput, PsiClass psiClass, List<PsiMethod> api) throws NoApiException {
        System.out.printf("Generating API from %d methods...%n", api.size());
        if (api.isEmpty()) {
            throw new NoApiException(format("No %sAPI for %s", userInput.getType() == InputType.TYPE ? "static " : "", psiClass.getName()));
        }
        return api.stream()
                .filter(method -> method.getName().startsWith(userInput.getFilter()))
                .sorted(SortingUtils.getSorting(config.getSortingType()))
                .map(method -> strategyMap.get(config.getApiViewType())
                        .generateApiLine(userInput, method))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
