package com.joymutlu.apiexplorer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.joymutlu.apiexplorer.util.EditorConstants.NEW_LINE;
import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_STRING_PREFIX;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public final class ImportUtils {
    public static final List<String> DEFAULT_PACKAGES = new ArrayList<>(asList(
            "java.lang",
            "java.lang.reflect"
    ));

    public static List<String> getImportListWithDefaults(String fileText) {
        final List<String> importList = getImportList(fileText);
        importList.add(getCurrentPackage(fileText));
        importList.addAll(DEFAULT_PACKAGES);
        return importList;
    }

    public static List<String> getImportList(String fileText) {
        return Arrays.stream(fileText.split("\n"))
                .filter(line -> line.startsWith(EditorConstants.IMPORT_STRING_PREFIX))
                .collect(toList());
    }

    public static String getCurrentPackage(String fileText) {
        return fileText.substring(
                PACKAGE_STRING_PREFIX.length(),
                fileText.indexOf(NEW_LINE) - 1);
    }
}
