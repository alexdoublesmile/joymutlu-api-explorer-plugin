package com.joymutlu.apiexplorer.model;

import java.util.List;

public record OldMethodDeclaration(String name, List<Class<?>> argumentsList) {
}
