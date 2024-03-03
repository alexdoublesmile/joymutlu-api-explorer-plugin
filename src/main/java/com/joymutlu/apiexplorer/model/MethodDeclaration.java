package com.joymutlu.apiexplorer.model;

import java.util.List;

public record MethodDeclaration(String name, List<Class<?>> argumentsList) {
}
