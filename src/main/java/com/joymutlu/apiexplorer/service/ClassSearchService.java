package com.joymutlu.apiexplorer.service;

import com.joymutlu.apiexplorer.exception.NoClassException;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.util.ClassUtils;
import com.joymutlu.apiexplorer.util.ImportUtils;
import com.joymutlu.apiexplorer.util.StringUtils;

import java.util.List;
import java.util.Optional;

public final class ClassSearchService {
    public Class<?> findClass(List<String> importList, String className) throws NoClassException, NoImportException {
        final Optional<Class<?>> maybeClass = ClassUtils.findClassByName(
                ImportUtils.getFullClassName(importList, className));

        return maybeClass.isPresent()
                ? maybeClass.get()
                : ClassUtils.findClassByNameList(ImportUtils.getAsteriskDeclarations(importList), className)
                .orElseThrow(() -> new NoImportException("Import for '" + className + "' class not found. Declare import first"));
    }

    public String findClassName(ExploreContext ctx, CharSequence code) throws UnknownInputException, NoInitializingLineException {
        switch (ctx.getInputType()) {
            case TYPE: return ctx.getUserInput();
            case OBJECT: return defineClassFromObject(ctx, code);
            default: throw new UnknownInputException();
        }
    }

    private String defineClassFromObject(ExploreContext ctx, CharSequence editorCode) throws NoInitializingLineException {
        final String input = ctx.getUserInput();
        System.out.printf("Defining object [%s] type...%n", input);
        String line = StringUtils.findInitializingLine(editorCode, input)
                .orElseThrow(NoInitializingLineException::new);
        System.out.printf("Initialization line: [%s]%n", line);

        String objectType = "";
        final String[] lineElements = line.split(" ");
        for (int i = 0; i < lineElements.length; i++) {
            String element = lineElements[i];
            if (element.equals(input)) {
                objectType = lineElements[i - 1];
            }
        }

        for (int i = 0; i < objectType.length(); i++) {
            final char ch = objectType.charAt(i);
            if (ch == '<') {
                objectType = objectType.substring(0, i);
            }
        }
        return objectType;
    }
}
