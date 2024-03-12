package com.joymutlu.apiexplorer.strategy.classfind;

import com.joymutlu.apiexplorer.model.InputType;

import java.util.Map;

public final class ClassFindStrategyFactory {
    private static final Map<InputType, ClassFindStrategy> classFindStrategyMap = InputType.buildclassFindStrategyMap();

    public static ClassFindStrategy getStrategy(InputType inputType) {
        final ClassFindStrategy strategy = classFindStrategyMap.get(inputType);
        System.out.printf("Starting '%s'...%n", strategy.getName());
        return strategy;
    }
}
