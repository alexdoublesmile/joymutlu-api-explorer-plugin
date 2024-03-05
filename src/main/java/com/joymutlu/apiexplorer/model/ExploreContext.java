package com.joymutlu.apiexplorer.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.util.PsiUtils;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.isLowerCase;
import static java.lang.String.format;
import static java.util.Comparator.comparing;

public class ExploreContext {
    private final ExploreConfig config;
    private UserInput userInput;
    private InputType inputType;
    private String indent;
    private PsiClass exploreClass;
    private Map<MethodDeclaration, PsiMethod> api = new HashMap<>();

    public ExploreContext(ExploreConfig config) {
        this.config = config;
    }

    public UserInput getUserInput() {
        return userInput;
    }

    public void setUserInput(UserInput userInput) {
        this.userInput = userInput;
        setInputType(userInput.value());
    }

    public String getIndent() {
        return indent;
    }

    public void setIndent(int spacesNumber) {
        indent = format("%-" + spacesNumber + "s", "");
    }

    public PsiClass getExploreClass() {
        return exploreClass;
    }

    public InputType getInputType() {
        return inputType;
    }

    private void setInputType(String input) {
        inputType = StringUtils.isUnknown(input)
                ? InputType.UNKNOWN
                : isLowerCase(input.charAt(0)) ? InputType.OBJECT : InputType.TYPE;
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

    public void setApi(PsiClass clazz) throws UnknownInputException {
        exploreClass = clazz;
        api = switch (inputType) {
            case TYPE -> filterDeprecated(filterUnique(
                    PsiUtils.getStaticApi(clazz)));
            case OBJECT -> filterDeprecated(filterUnique(
                    PsiUtils.getVirtualApi(clazz, config.withParentApi())));
            default -> throw new UnknownInputException();
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
                            : !name.startsWith("compare") ? 6
                            : 7;
                }).thenComparing(PsiMethod::getName);
    }
}
