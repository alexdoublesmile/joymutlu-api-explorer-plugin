package com.joymutlu.apiexplorer.service;

import com.joymutlu.apiexplorer.exception.NoApiException;
import com.joymutlu.apiexplorer.model.ApiViewType;
import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.model.InputType;
import com.joymutlu.apiexplorer.strategy.CodeGenerationStrategy;

import java.util.Map;

import static java.lang.String.format;

public final class CodeGenerationService {
    private final Map<ApiViewType, CodeGenerationStrategy> strategyMap;

    public CodeGenerationService() {
        this.strategyMap = ApiViewType.getGenerationStrategyMap();
    }

    public String generateApiString(ExploreContext ctx) throws NoApiException {
        if (ctx.getApi().isEmpty()) {
            throw new NoApiException(format("No %sAPI for %s", ctx.getInputType() == InputType.TYPE ? "static " : "", ctx.getExploreClass()));
        }
        return ctx.getApi().stream()
                .map(method -> strategyMap.get(ctx.getApiViewType()).generateApiLine(ctx, method))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

}
