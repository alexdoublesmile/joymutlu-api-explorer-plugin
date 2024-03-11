package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

public class PsiClassService {
    private final Project project;

    public PsiClassService(Project project) {
        this.project = project;
    }

    public PsiClass findPsiClass(String className) {
        return null;
    }
}
