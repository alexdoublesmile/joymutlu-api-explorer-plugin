package com.joymutlu.apiexplorer.strategy;

import com.joymutlu.apiexplorer.model.ExploreContext;

import java.lang.reflect.Method;

public interface CodeGenerationStrategy {
    String generateApiLine(ExploreContext ctx, Method method);
}
