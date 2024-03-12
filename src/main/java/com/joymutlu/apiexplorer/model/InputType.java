package com.joymutlu.apiexplorer.model;

import com.joymutlu.apiexplorer.strategy.classfind.*;

import java.util.HashMap;
import java.util.Map;

public enum InputType {
    TYPE {
        @Override
        public ClassFindStrategy getClassFindStrategy() {
            return new ExplicitClassFindStrategy();
        }
    }, OBJECT {
        @Override
        public ClassFindStrategy getClassFindStrategy() {
            return new ClassByObjectFindStrategy();
        }
    }, STATIC_METHOD {
        @Override
        public ClassFindStrategy getClassFindStrategy() {
            return new ClassByMethodFindStrategy();
        }
    }, VIRTUAL_METHOD {
        @Override
        public ClassFindStrategy getClassFindStrategy() {
            return new ClassByMethodFindStrategy();
        }
    }, UNKNOWN {
        @Override
        public ClassFindStrategy getClassFindStrategy() {
            return new UnknownStrategy();
        }
    };

    public static Map<InputType, ClassFindStrategy> buildclassFindStrategyMap() {
        final Map<InputType, ClassFindStrategy> map = new HashMap<>();
        for (InputType inputType : values()) {
            map.put(inputType, inputType.getClassFindStrategy());
        }
        return map;
    }
    public abstract ClassFindStrategy getClassFindStrategy();
}
