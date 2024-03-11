package com.joymutlu.apiexplorer.model;

import com.joymutlu.apiexplorer.strategy.codegeneration.CodeGenerationStrategy;
import com.joymutlu.apiexplorer.strategy.codegeneration.FullDeclarationGenerationStrategy;
import com.joymutlu.apiexplorer.strategy.codegeneration.MethodCallGenerationStrategy;
import com.joymutlu.apiexplorer.strategy.codegeneration.MethodNameGenerationStrategy;

import java.util.HashMap;
import java.util.Map;

public enum ApiViewType {
    METHOD_NAME {
        @Override
        public CodeGenerationStrategy getCodeGenerationStrategy() {
            return new MethodNameGenerationStrategy();
        }
    }, METHOD_CALL {
        @Override
        public CodeGenerationStrategy getCodeGenerationStrategy() {
            return new MethodCallGenerationStrategy();
        }
    }, FULL {
        @Override
        public CodeGenerationStrategy getCodeGenerationStrategy() {
            return new FullDeclarationGenerationStrategy();
        }
    };

    public abstract CodeGenerationStrategy getCodeGenerationStrategy();

    public static Map<ApiViewType, CodeGenerationStrategy> getGenerationStrategyMap() {
        final Map<ApiViewType, CodeGenerationStrategy> strategyMap = new HashMap();
        for (ApiViewType viewType : values()) {
            strategyMap.put(viewType, viewType.getCodeGenerationStrategy());
        }
        return strategyMap;
    }
}
