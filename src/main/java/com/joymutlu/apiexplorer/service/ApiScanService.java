package com.joymutlu.apiexplorer.service;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.config.PluginConfig;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.MethodDeclaration;
import com.joymutlu.apiexplorer.util.PsiUtils;

import java.util.Map;

public final class ApiScanService {
    private final UserInputService userInputService;
    private final PluginConfig config;

    public ApiScanService(UserInputService userInputService, PluginConfig config) {
        this.userInputService = userInputService;
        this.config = config;
    }

    public Map<MethodDeclaration, PsiMethod> findApi(PsiClass psiClass) throws UnknownInputException {
        switch (userInputService.getUserInput().getType()) {
            case TYPE: return filterDeprecated(filterUnique(
                    PsiUtils.getStaticApi(psiClass)));
            case OBJECT: return filterDeprecated(filterUnique(
                    PsiUtils.getVirtualApi(psiClass, config.withParentApi())));
            default: throw new UnknownInputException();
        }
    }

    private Map<MethodDeclaration, PsiMethod> filterDeprecated(Map<MethodDeclaration, PsiMethod> methods) {
        return config.withDeprecated() ? methods : PsiUtils.removeDeprecated(methods);
    }

    private Map<MethodDeclaration, PsiMethod> filterUnique(Map<MethodDeclaration, PsiMethod> methods) {
        return config.withArguments() ? methods : PsiUtils.removeOverloads(methods);
    }
}
