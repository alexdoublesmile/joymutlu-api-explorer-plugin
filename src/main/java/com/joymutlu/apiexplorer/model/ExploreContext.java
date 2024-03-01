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
    private final boolean needParams;
    private final boolean needReturnValues;
    private final boolean needParentApi;
    private final ApiViewType apiViewType;
    private String userInput;
    private InputType inputType;
    private String indent;
    private Class<?> exploreClass;
    private List<Method> api = new ArrayList<>();

    public ExploreContext(
            boolean needParams,
            boolean needReturnValues,
        boolean needParentApi
    ) {
        this.needParams = needParams;
        this.needReturnValues = needReturnValues;
        this.needParentApi = needParentApi;
        apiViewType = needParams
                ? this.needReturnValues ? ApiViewType.FULL : ApiViewType.METHOD_CALL
                : ApiViewType.METHOD_NAME;
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
            case TYPE: api = filterApi(getStaticApi(clazz));
            case OBJECT: api = filterApi(getVirtualApi(clazz));
        };
    }

    private List<Method> filterApi(List<Method> methods) {
        return needParams ? methods : getWithoutOverloaded(methods);
    }

    private List<Method> getWithoutOverloaded(List<Method> methods) {
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
                .collect(Collectors.toList());

        final Class<?> parent = exploreClass.getSuperclass();
        if (needParentApi && parent != null) {
            result.addAll(getVirtualApi(parent));
        }
        return result;
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
        return apiViewType;
    }
}
