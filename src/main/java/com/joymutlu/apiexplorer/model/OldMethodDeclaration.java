package com.joymutlu.apiexplorer.model;

import java.util.List;
import java.util.Objects;

public final class OldMethodDeclaration {
    private final String name;
    private final List<Class<?>> argumentsList;

    public OldMethodDeclaration(String name, List<Class<?>> argumentsList) {
        this.name = name;
        this.argumentsList = argumentsList;
    }

    public String getName() {
        return name;
    }

    public List<Class<?>> getArgumentsList() {
        return argumentsList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        OldMethodDeclaration that = (OldMethodDeclaration) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.argumentsList, that.argumentsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, argumentsList);
    }

    @Override
    public String toString() {
        return "OldMethodDeclaration[" +
                "name=" + name + ", " +
                "argumentsList=" + argumentsList + ']';
    }

}
