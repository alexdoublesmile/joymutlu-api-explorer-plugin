package com.joymutlu.apiexplorer.service;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.config.PluginConfig;
import com.joymutlu.apiexplorer.exception.GenericSearchException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.util.PsiUtils;

import java.util.List;

import static java.lang.String.format;

public final class ApiScanService {
    private final PluginConfig config;

    public ApiScanService(PluginConfig config) {
        this.config = config;
    }

    public List<PsiMethod> findApi(UserInput userInput, PsiClass psiClass) throws UnknownInputException, GenericSearchException {
        final String[] psiTypeNameElements = psiClass.toString().split(":");
        final String psiType = psiTypeNameElements[0];
        final String psiName = psiTypeNameElements[1];
        if (psiType.equals("PsiTypeParameter")) {
            throw new GenericSearchException(format("You can't load methods from Generic [%s] type", psiName));
        }
        switch (userInput.getApiType()) {
            case STATIC: return filterDeprecated(filterUnique(
                    PsiUtils.getStaticApi(psiClass)));
            case VIRTUAL: return filterDeprecated(filterUnique(
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
