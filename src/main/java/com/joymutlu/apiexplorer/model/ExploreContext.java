package com.joymutlu.apiexplorer.model;

import com.joymutlu.apiexplorer.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.joymutlu.apiexplorer.model.InputType.*;
import static java.lang.Character.isLetter;
import static java.lang.Character.isLowerCase;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ExploreContext {
    private List<Method> API = new ArrayList<>();
    private Class<?> exploreClass;

    private String userInput;
    private InputType inputType;
    private String indent;

    private boolean needParams;
    private boolean needParentApi = true;
    private boolean needReturnValues;

    public Class<?> getExploreClass() {
        return exploreClass;
    }

    public boolean needReturnValues() {
        return needReturnValues;
    }

    public boolean needParams() {
        return needParams;
    }

    public void setNeedReturnValues(boolean needReturnValues) {
        this.needReturnValues = needReturnValues;
    }

    public void setNeedParentApi(boolean needParentApi) {
        this.needParentApi = needParentApi;
    }

    public void setNeedParams(boolean needParams) {
        this.needParams = needParams;
    }

    public String getIndent() {
        return indent;
    }

    public InputType getInputType() {
        return inputType;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
        inputType = setInputType(userInput);
    }

    private InputType setInputType(String input) {
        char firstSymbol;
        if (!input.isBlank() && isLetter(firstSymbol = input.charAt(0))) {
            if (isLowerCase(firstSymbol)) {
                return OBJECT;
            } else {
                return TYPE;
            }
        } else {
            return UNKNOWN;
        }
    }

    public void setIndent(int spacesNumber) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < spacesNumber; i++) {
            stringBuilder.append(" ");
        }
        indent = stringBuilder.toString();
    }

    public void buildApi(Class<?> clazz) {
        exploreClass = clazz;
        switch (inputType) {
            case TYPE: setApi(addStaticApi(clazz));
                break;
            case OBJECT: setApi(getVirtualApi(clazz));
                break;
            default:
        }
    }

    private void setApi(List<Method> methods) {
        API = needParams ? methods : getWithoutOverloaded(methods);
    }

    private List<Method> getWithoutOverloaded(List<Method> methods) {
        System.out.println("Removing overloaded methods...");
        return new ArrayList<>(methods.stream()
                .collect(toMap(Method::getName, identity(), (m1, m2) -> m1))
                .values());
    }

    private List<Method> addStaticApi(Class<?> clazz) {
        System.out.printf("Collecting all public static methods from %s...%n", clazz);
        return Arrays.stream(clazz.getMethods())
                .filter(ReflectionUtils::isStatic)
                .collect(toList());
    }

    private List<Method> getVirtualApi(Class<?> clazz) {
        System.out.printf("Collecting all public methods from %s...%n", clazz);
        final List<Method> result = Arrays
                .stream(clazz.getMethods())
                .filter(ReflectionUtils::isVirtual)
                .collect(Collectors.toList());
        final Class<?> parent = clazz.getSuperclass();
        if (needParentApi && parent != null) {
            result.addAll(getVirtualApi(parent));
        }
        return result;
    }

    public List<Method> getAPI() {
        return new ArrayList<>(API);
    }
}
