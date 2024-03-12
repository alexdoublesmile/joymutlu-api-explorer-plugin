package com.joymutlu.apiexplorer.strategy.classfind;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.NoMethodException;
import com.joymutlu.apiexplorer.exception.PrimitiveTypeException;
import com.joymutlu.apiexplorer.model.InputType;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.service.ClassFindService;
import com.joymutlu.apiexplorer.util.JavaFileUtils;
import com.joymutlu.apiexplorer.util.PsiUtils;
import com.joymutlu.apiexplorer.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class ClassByMethodFindStrategy implements ClassFindStrategy {
    @Override
    public PsiClass findClass(UserInput userInput, String fileText, Project project)
            throws NoImportException, PrimitiveTypeException, NoInitializingLineException, NoMethodException {

        final String[] callElements = userInput.getValue().split("\\.");
        String invokerObj = callElements[0];
        List<String> invokeChain = Arrays.stream(callElements)
                .skip(1)
                .map(StringUtils::getMethodNameFromCall)
                .collect(toList());

        String invokerName = userInput.getType() == InputType.STATIC_METHOD
                ? invokerObj :
                JavaFileUtils.findClassNameByObject(fileText, invokerObj);
        List<String> importList = JavaFileUtils.getImportListWithDefaults(fileText);
        PsiClass invoker = ClassFindService.findClass(invokerName, importList, project);
        System.out.printf("Root invoker type [%s]%n", invoker);

        for (String methodName : invokeChain) {
            final PsiMethod method = resolveMethod(invoker, methodName);
            final PsiType returnType = resolveReturnType(method);
            System.out.printf("Next return type [%s]%n", returnType);
            invoker = PsiUtil.resolveClassInType(returnType);
        }
        return invoker;
    }

    @Override
    public String getName() {
        return "Method Chain API Searching Strategy";
    }

    private PsiType resolveReturnType(PsiMethod method) throws NoMethodException, PrimitiveTypeException {
        PsiType returnType = method.getReturnType();
        if (returnType == null) {
            throw new NoMethodException("You can't load methods from constructor");
        }
        if (StringUtils.isInvalidReturnType(returnType.getPresentableText())) {
            throw new PrimitiveTypeException("You can't load methods from '" + returnType.getPresentableText() + "' type");
        }
        if (StringUtils.isArrayType(returnType.getPresentableText())) {
            throw new NoMethodException("There are no methods for array");
        }
        return returnType;
    }

    private PsiMethod resolveMethod(@NotNull PsiClass invoker, String methodName) throws NoMethodException {
        PsiMethod[] methods = invoker.findMethodsByName(methodName, false);
        if (methods.length == 0) {
            methods = invoker.findMethodsByName(methodName, true);
            if (methods.length == 0) {
                throw new NoMethodException(format("Class [%s] hasn't any method with name [%s]", invoker.getName(), methodName));
            }
        }
        if (methods.length == 1) {
            return methods[0];
        }
        return chooseProperMethod(methods);
    }

    private PsiMethod chooseProperMethod(PsiMethod[] methods) {
        final Map<Boolean, List<PsiMethod>> deprecatedMap = Arrays.stream(methods)
                .filter(method -> isRealType(method.getReturnType()))
                .collect(groupingBy(PsiUtils::isDeprecated));

        if (deprecatedMap.size() != 0) {
            if (deprecatedMap.get(false).size() > 0) {
                return deprecatedMap.get(false).get(0);
            } else {
                return deprecatedMap.get(true).get(0);
            }
        }
        return methods[0];
    }

    private boolean isRealType(PsiType type) {
        return type != null && StringUtils.isValidReturnType(type.getPresentableText());
    }
}
