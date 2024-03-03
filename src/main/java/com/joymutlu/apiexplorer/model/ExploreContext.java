package com.joymutlu.apiexplorer.model;

import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.util.ReflectionUtils;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Character.isLowerCase;
import static java.lang.String.format;

public class ExploreContext {
    private final ExploreConfig config;
    private UserInput userInput;
    private InputType inputType;
    private String indent;
    private Class<?> exploreClass;
    private Map<MethodDeclaration, Method> api = new HashMap<>();

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

    public Class<?> getExploreClass() {
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

    public Map<MethodDeclaration, Method> getApi() {
        return new HashMap<>(api);
    }

    public void setApi(Class<?> clazz) throws UnknownInputException {
        exploreClass = clazz;
        api = switch (inputType) {
            case TYPE -> filterDeprecated(filterUnique(
                    ReflectionUtils.getStaticApi(clazz)));
            case OBJECT -> filterDeprecated(filterUnique(
                    ReflectionUtils.getVirtualApi(clazz, config.withParentApi())));
            default -> throw new UnknownInputException();
        };
    }

    private Map<MethodDeclaration, Method> filterDeprecated(Map<MethodDeclaration, Method> methods) {
        return config.withDeprecated() ? methods : ReflectionUtils.removeDeprecated(methods);
    }

    private Map<MethodDeclaration, Method> filterUnique(Map<MethodDeclaration, Method> methods) {
        return config.withArguments() ? methods : ReflectionUtils.removeOverloads(methods);
    }
}
