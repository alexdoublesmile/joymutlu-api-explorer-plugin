package com.joymutlu.apiexplorer.util;

import java.util.List;

public final class ImportUtils {
    public static void addDefaultPackages(List<String> classPathApplicants) {
        classPathApplicants.add("java.lang");
    }
}
