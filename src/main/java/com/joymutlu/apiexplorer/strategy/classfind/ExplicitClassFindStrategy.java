package com.joymutlu.apiexplorer.strategy.classfind;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.PrimitiveTypeException;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.util.ImportUtils;
import com.joymutlu.apiexplorer.util.PsiUtils;

public class ExplicitClassFindStrategy implements ClassFindStrategy {
    @Override
    public PsiClass findClass(UserInput userInput, String fileText, Project project)
            throws NoImportException, PrimitiveTypeException {

        return PsiUtils.findClass(
                userInput.getValue(),
                ImportUtils.getImportListWithDefaults(fileText),
                project);
    }

    @Override
    public String getName() {
        return "Class Static API Searching Strategy";
    }
}
