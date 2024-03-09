package com.joymutlu.apiexplorer.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.util.PsiUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Comparator.comparing;

public class ExploreContext {
    private final ExploreConfig config;
    private UserInputInfo inputInfo;
    private String indent;
    private PsiClass exploredClass;
    private Map<MethodDeclaration, PsiMethod> api = new HashMap<>();

    public ExploreContext(ExploreConfig config) {
        this.config = config;
    }

    public UserInputInfo getUserInput() {
        return inputInfo;
    }

    public void setUserInput(UserInputInfo userInputInfo) {
        this.inputInfo = userInputInfo;
    }

    public String getIndent() {
        return indent;
    }

    public void setIndent(int spacesNumber) {
        indent = format("%-" + spacesNumber + "s", "");
    }

    public PsiClass getExploredClass() {
        return exploredClass;
    }

    public ApiViewType getApiViewType() {
        return config.getApiViewType();
    }

    public ExploreConfig getConfig() {
        return config;
    }

    public Map<MethodDeclaration, PsiMethod> getApi() {
        return new HashMap<>(api);
    }

    public void setClassWithApi(PsiClass clazz) throws UnknownInputException {
        exploredClass = clazz;
        switch (inputInfo.getInputType()) {
            case TYPE: api = filterDeprecated(filterUnique(
                    PsiUtils.getStaticApi(clazz)));
            break;
            case OBJECT: api = filterDeprecated(filterUnique(
                    PsiUtils.getVirtualApi(clazz, config.withParentApi())));
            break;
            default: throw new UnknownInputException();
        };
    }

    private Map<MethodDeclaration, PsiMethod> filterDeprecated(Map<MethodDeclaration, PsiMethod> methods) {
        return config.withDeprecated() ? methods : PsiUtils.removeDeprecated(methods);
    }

    private Map<MethodDeclaration, PsiMethod> filterUnique(Map<MethodDeclaration, PsiMethod> methods) {
        return config.withArguments() ? methods : PsiUtils.removeOverloads(methods);
    }

    public Comparator<? super PsiMethod> getSorting() {
        return config.withNaturalSorting()
                ? comparing(PsiMethod::getName)
                : comparing((PsiMethod method) -> {
                    String name = method.getName();
                    return name.startsWith("is") ? 1
                            : name.startsWith("has") ? 2
                            : name.startsWith("get") ? 3
                            : name.startsWith("set") ? 4
                            : name.startsWith("to") ? 5
                            : !name.startsWith("compare")
                            && !name.startsWith("close") ? 6
                            : !name.startsWith("close") ? 7
                            : 8;
                }).thenComparing(PsiMethod::getName);
    }
}
