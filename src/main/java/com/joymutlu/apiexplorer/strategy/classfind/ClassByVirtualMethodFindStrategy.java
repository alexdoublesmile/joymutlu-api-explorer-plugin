package com.joymutlu.apiexplorer.strategy.classfind;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.PrimitiveTypeException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.service.ClassFindService;
import com.joymutlu.apiexplorer.util.JavaFileUtils;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ClassByVirtualMethodFindStrategy implements ClassFindStrategy {
    @Override
    public PsiClass findClass(UserInput userInput, String fileText, Project project) throws NoImportException, PrimitiveTypeException, NoInitializingLineException, UnknownInputException {
        final String[] inputElements = userInput.getValue().split("\\.");
        String objectName = inputElements[0];
        List<String> inputChain = Arrays.stream(inputElements)
                .skip(1)
                .map(StringUtils::getMethodNameFromCall)
                .collect(toList());

        String invokerName = JavaFileUtils.findClassNameByObject(fileText, objectName);
        List<String> importList = JavaFileUtils.getFullImportList(fileText);
        PsiClass invoker = ClassFindService.findClass(invokerName, importList, project);
        System.out.printf("Found caller type [%s]%n", invoker);

        for (String methodName : inputChain) {
            final PsiMethod psiMethod = invoker.findMethodsByName(methodName, false)[0];
            final PsiType returnPsiType = psiMethod.getReturnType();
            // TODO: 11.03.2024 check void & primitives
            invoker = PsiUtil.resolveClassInType(returnPsiType);
            System.out.printf("Found invoker type [%s]%n", invoker);
        }
        return invoker;
    }
}
