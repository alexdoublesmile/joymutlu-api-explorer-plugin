package com.joymutlu.apiexplorer.service;

import com.joymutlu.apiexplorer.exception.NoClassException;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NotImplementedException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.util.ClassUtils;
import com.joymutlu.apiexplorer.util.ImportUtils;

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

    public String findClassName(ExploreContext ctx, CharSequence code) throws UnknownInputException, NotImplementedException {
        switch (ctx.getInputType()) {
            case TYPE: return ctx.getUserInput();
            case OBJECT: return defineClassFromObject(ctx, code);
            default: throw new UnknownInputException();
        }
    }

    private String defineClassFromObject(ExploreContext ctx, CharSequence code) throws NotImplementedException {
        System.out.printf("Trying to define object [%s]...%n", ctx.getUserInput());
        // TODO: 28.02.2024 find place object was init
        // TODO: 28.02.2024 return type of this expression
        throw new NotImplementedException("Objects defining is not implemented in current version");
    }
}
