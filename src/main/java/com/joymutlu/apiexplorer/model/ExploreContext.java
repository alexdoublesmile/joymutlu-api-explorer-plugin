package com.joymutlu.apiexplorer.model;

import com.joymutlu.apiexplorer.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.joymutlu.apiexplorer.model.InputType.*;
import static java.lang.Character.isLetter;
import static java.lang.Character.isLowerCase;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ExploreContext {
    private final ExploreConfig config;
    private String userInput;
    private InputType inputType;
    private String indent;
    private Class<?> exploreClass;
    private List<Method> api = new ArrayList<>();

    public ExploreContext(ExploreConfig config) {
        this.config = config;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
        inputType = setInputType(userInput);
    }

    public String getIndent() {
        return indent;
    }

    public void setIndent(int spacesNumber) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < spacesNumber; i++) {
            stringBuilder.append(" ");
        }
        indent = stringBuilder.toString();
    }

    public Class<?> getExploreClass() {
        return exploreClass;
    }

    public InputType getInputType() {
        return inputType;
    }

    public List<Method> getApi() {
        return new ArrayList<>(api);
    }

    public void setApi(Class<?> clazz) {
        exploreClass = clazz;
        switch (inputType) {
            case TYPE: api = filterDeprecated(filterUnique(getStaticApi(clazz)));
            break;
            case OBJECT: api = filterDeprecated(filterUnique(getVirtualApi(clazz)));
        };
    }

    private List<Method> filterDeprecated(List<Method> methods) {
        return config.withDeprecated() ? methods : removeDeprecated(methods);
    }

    private List<Method> filterUnique(List<Method> methods) {
        return config.withArguments() ? methods : removeOverloads(methods);
    }

    private List<Method> removeDeprecated(List<Method> methods) {
        System.out.println("Removing deprecated methods...");
        return methods.stream()
                .filter(ReflectionUtils::isNotDeprecated)
                .collect(toList());
    }

    private List<Method> removeOverloads(List<Method> methods) {
        System.out.println("Removing overloaded methods...");
        return new ArrayList<>(methods.stream()
                .collect(toMap(Method::getName, identity(), (m1, m2) -> m1))
                .values());
    }

    private List<Method> getStaticApi(Class<?> clazz) {
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
                .filter(ReflectionUtils::isNotAbstract)
                .filter(ReflectionUtils::isNotObjectMethod)
                .collect(toList());

        final Class<?> parent = clazz.getSuperclass();
        if (config.withParentApi() && !isObjectClass(parent)) {
            final List<Method> parentMethods = getVirtualApi(parent);
            System.out.printf("Adding %d public non-abstract methods from %s to %s%n", parentMethods.size(), parent, clazz);
            result.addAll(parentMethods);
        }
        return result;
    }

    private boolean isObjectClass(Class<?> parent) {
        return parent.getName().equals("java.lang.Object");
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

    public ApiViewType getApiViewType() {
        return config.getApiViewType();
    }

    public ExploreConfig getConfig() {
        return config;
    }
}
