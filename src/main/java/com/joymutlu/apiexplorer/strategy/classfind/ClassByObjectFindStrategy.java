package com.joymutlu.apiexplorer.strategy.classfind;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.PrimitiveTypeException;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.util.ImportUtils;
import com.joymutlu.apiexplorer.util.TypeSearchUtils;
import com.joymutlu.apiexplorer.util.PsiUtils;

public class ClassByObjectFindStrategy implements ClassFindStrategy {
    @Override
    public PsiClass findClass(UserInput userInput, String fileText, Project project)
            throws NoImportException, PrimitiveTypeException, NoInitializingLineException {

        return PsiUtils.findClass(
                TypeSearchUtils.findTypeByReference(fileText, userInput.getValue()),
                ImportUtils.getImportListWithDefaults(fileText),
                project);
    }

    @Override
    public String getName() {
        return "Object Reference API Searching Strategy";
    }
}
