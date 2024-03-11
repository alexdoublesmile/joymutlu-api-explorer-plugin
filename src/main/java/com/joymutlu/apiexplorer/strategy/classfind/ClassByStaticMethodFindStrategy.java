package com.joymutlu.apiexplorer.strategy.classfind;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.PrimitiveTypeException;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.service.ClassFindService;
import com.joymutlu.apiexplorer.util.JavaFileUtils;
import com.joymutlu.apiexplorer.util.StringUtils;

public class ClassByStaticMethodFindStrategy implements ClassFindStrategy {
    @Override
    public PsiClass findClass(UserInput userInput, String fileText, Project project) throws NoImportException, PrimitiveTypeException, NoInitializingLineException {
        System.out.printf("Defining static method [%s] return type...%n", userInput);
        final String[] inputElements = userInput.getValue().split("\\.");
        String invokerName = inputElements[0];
        String methodName = StringUtils.getMethodNameFromCall(inputElements[1]);

        final PsiClass invoker = ClassFindService.findClass(invokerName, JavaFileUtils.getFullImportList(fileText), project);
        System.out.printf("Found caller type [%s]%n", invoker);

        // TODO: 11.03.2024 add multimethod invoke 
        final PsiMethod psiMethod = invoker.findMethodsByName(methodName, false)[0];
        final PsiType returnPsiType = psiMethod.getReturnType();
        return PsiUtil.resolveClassInType(returnPsiType);
    }
}
