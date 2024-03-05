package com.joymutlu.apiexplorer.model;

import com.intellij.psi.PsiParameterList;

public record MethodDeclaration(String name, PsiParameterList argumentsList) {
}
