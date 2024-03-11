package com.joymutlu.apiexplorer.strategy.codegeneration;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.strategy.codegeneration.CodeGenerationStrategy;
import com.joymutlu.apiexplorer.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class FullDeclarationGenerationStrategy implements CodeGenerationStrategy {
    private static final String ARRAY_DEFAULT_TYPE = "[Ljava.lang.Object;";
    private static final String ARRAY_CUSTOM_TYPE = "Object[]";
    private static final String REFERENCE_SUFFIX_START = "By";
    private static final String REFERENCE_SUFFIX_DELIMITER = "And";

    @Override
    public String generateApiLine(UserInput userInput, PsiMethod method) {
        return new StringBuilder()
                .append(resolveReference(method))
                .append(userInput)
                .append(".")
                .append(method.getName())
                .append("(")
                .append(StringUtils.getArgsString(
                        stream(method.getParameterList().getParameters())
                                .map(param -> param.getType().toString())
                                .collect(toList())))
                .append(");")
                .append("\n")
                .append(userInput.getIndent())
                .toString();
    }

    private String resolveReference(PsiMethod method) {
        final @Nullable PsiType returnType = method.getReturnType();
        if (returnType.toString().equals("void")) {
            return "";
        }
        return new StringBuilder()
                .append(resolveReturnType(returnType))
                .append(" ")
                .append(method.getName())
                .append(resolveSuffix(method))
                .append(" = ")
                .toString();
    }

    private String resolveReturnType(PsiType returnType) {
        return returnType.toString().equals(ARRAY_DEFAULT_TYPE)
                ? ARRAY_CUSTOM_TYPE
                : returnType.toString();
    }

    private String resolveSuffix(PsiMethod method) {
        if (method.getParameterList().getParametersCount() == 0) {
            return "";
        }
        final String result = REFERENCE_SUFFIX_START + stream(method.getParameterList().getParameters())
                .map(PsiParameter::getType)
                .map(paramType -> paramType.toString().replace("[]", "Array"))
                .collect(StringBuilder::new, (sb1, sb2) -> sb1.append(sb2).append(REFERENCE_SUFFIX_DELIMITER), StringBuilder::append);
        return result.substring(0, result.length() - REFERENCE_SUFFIX_DELIMITER.length());
    }
}
