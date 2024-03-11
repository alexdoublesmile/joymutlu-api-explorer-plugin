package com.joymutlu.apiexplorer.strategy.classfind;

import com.joymutlu.apiexplorer.model.InputType;
import com.joymutlu.apiexplorer.strategy.classfind.ClassFindStrategy;

import java.util.Map;

public final class ClassFindStrategyFactory {
    private static final Map<InputType, ClassFindStrategy> classFindStrategyMap = InputType.buildclassFindStrategyMap();

    public static ClassFindStrategy getStrategy(InputType inputType) {
        return classFindStrategyMap.get(inputType);
    }
}
