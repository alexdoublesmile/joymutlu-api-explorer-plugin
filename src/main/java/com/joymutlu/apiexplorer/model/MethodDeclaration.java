package com.joymutlu.apiexplorer.model;

import com.intellij.psi.PsiParameterList;

import java.util.Objects;

public final class MethodDeclaration {
    private final String name;
    private final PsiParameterList argumentsList;

    public MethodDeclaration(String name, PsiParameterList argumentsList) {
        this.name = name;
        this.argumentsList = argumentsList;
    }

    public String getName() {
        return name;
    }

    public PsiParameterList getArgumentsList() {
        return argumentsList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        MethodDeclaration that = (MethodDeclaration) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.argumentsList, that.argumentsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, argumentsList);
    }

    @Override
    public String toString() {
        return "MethodDeclaration[" +
                "name=" + name + ", " +
                "argumentsList=" + argumentsList + ']';
    }
}
