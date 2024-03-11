package com.joymutlu.apiexplorer.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.joymutlu.apiexplorer.util.ClassUtils.PRIMITIVE_SET;
import static java.lang.Character.isLetter;

public final class StringUtils {
    public static String getArgsString(List<String> argTypes) {
        String result = "";
        for (int i = 0; i < argTypes.size(); i++) {
            final String paramType = argTypes.get(i);
            result += getArgDefaultValue(paramType);
            if (i < argTypes.size() - 1) {
                result += ", ";
            }
        }
        return result;
    }

    private static String getArgDefaultValue(String paramType) {
        switch (paramType) {
            case "Byte" :
            case "byte" :
            case "Short" :
            case "short" :
            case "Integer" :
            case "int" : return "1";
            case "Long" :
            case "long" : return "1L";
            case "Float" :
            case "float" : return "1.0f";
            case "Double" :
            case "double" : return "1.0";
            case "Boolean" :
            case "boolean" : return "true";
            case "Character" :
            case "char" : return "'c'";
            case "CharSequence" :
            case "String" : return "\"str\"";
            default: return "null";
        }
    }

    public static Optional<String> findInitializingLine(String editorCode, String objectName) {
        return Arrays.stream(editorCode.split("\n"))
                .filter(line -> isInitLine(line, objectName))
                .findFirst();
    }

    private static boolean isInitLine(String line, String objectName) {
        return line.contains(" " + objectName + " ")
                || line.contains(" " + objectName + ";")
                || line.contains(" " + objectName + "=")
                || line.contains(" " + objectName + ",")
                || line.contains("..." + objectName + ")")
                || line.contains("..." + objectName + " ")
                || line.contains(" " + objectName + ")")
                || line.contains(" " + objectName);
    }

    public static String stripGenerics(String str) {
        return str.indexOf('<') != -1
                ? str.substring(0, str.indexOf('<'))
                : str;
    }

    public static boolean isGenericDeclaration(String str) {
        return str.endsWith(">");
    }

    public static boolean isUndefinedLowerType(String str) {
        return isNotPrimitive(str) && !str.equals("var");
    }

    public static boolean isNotPrimitive(String str) {
        return !PRIMITIVE_SET.contains(str);
    }

    public static boolean isLowerCase(String str) {
        return Character.isLowerCase(str.charAt(0));
    }

    public static boolean isUpperCase(String str) {
        return Character.isUpperCase(str.charAt(0));
    }

    public static boolean isDirtyVarargOrArray(String referenceStr, String reference) {
        return referenceStr.contains("..." + reference)
                || referenceStr.contains(reference + "[");
    }

    public static boolean isArray(String str) {
        return str.charAt(str.length() - 1) == ']';
    }

    public static boolean isUnknown(String str) {
        return str.isEmpty() || !isLetter(str.charAt(0));
    }

    public static String getMethodNameFromCall(String methodCall) {
        return methodCall.substring(0, methodCall.indexOf('('));
    }

    public static String resolveImport(String importStr, String className) {
        if (importStr.endsWith(EditorConstants.ASTERISK_DECLARATION)) {
            return importStr.substring(
                    EditorConstants.IMPORT_STRING_PREFIX.length(),
                    importStr.length() - EditorConstants.ASTERISK_DECLARATION.length());
        }
        if (importStr.startsWith("import ")) {
            if (importStr.endsWith(className + EditorConstants.DECLARATION_DELIMITER)
                    && EditorConstants.PACKAGE_DELIMITER == importStr.charAt(importStr.length() - className.length() - 2)) {
                return importStr.substring(
                        EditorConstants.IMPORT_STRING_PREFIX.length(),
                        importStr.length() - className.length() - 2);
            }
            return importStr.substring(
                    EditorConstants.IMPORT_STRING_PREFIX.length(),
                    importStr.length() - 1);
        }
        return importStr;
    }
}
