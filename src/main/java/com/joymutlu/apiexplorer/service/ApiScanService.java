package com.joymutlu.apiexplorer.service;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.config.PluginConfig;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.util.PsiUtils;

import java.util.List;

public final class ApiScanService {
    private final UserInputService userInputService;
    private final PluginConfig config;

    public ApiScanService(UserInputService userInputService, PluginConfig config) {
        this.userInputService = userInputService;
        this.config = config;
    }

    public List<PsiMethod> findApi(PsiClass psiClass) throws UnknownInputException {
        switch (userInputService.getUserInput().getType()) {
            case TYPE: return filterDeprecated(filterUnique(
                    PsiUtils.getStaticApi(psiClass)));
            case OBJECT: return filterDeprecated(filterUnique(
                    PsiUtils.getVirtualApi(psiClass, config.withParentApi())));
            default: throw new UnknownInputException();
        }
    }

    private List<PsiMethod> filterDeprecated(List<PsiMethod> methods) {
        return config.withDeprecated() ? methods : PsiUtils.removeDeprecated(methods);
    }

    private List<PsiMethod> filterUnique(List<PsiMethod> methods) {
        return config.withArguments() ? methods : PsiUtils.removeOverloads(methods);
    }
}
