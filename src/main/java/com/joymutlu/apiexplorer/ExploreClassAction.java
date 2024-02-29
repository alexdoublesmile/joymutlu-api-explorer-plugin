package com.joymutlu.apiexplorer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.joymutlu.apiexplorer.exception.NoClassException;
import com.joymutlu.apiexplorer.exception.NoImportException;
import com.joymutlu.apiexplorer.exception.NotImplementedException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.ExploreContext;

import java.lang.reflect.Method;
import java.util.*;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.intellij.openapi.ui.Messages.*;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class ExploreClassAction extends AnAction {
    private static final String IMPORT_STRING_PREFIX = "import ";
    private static final Character PACKAGE_DELIMITER = '.';
    private static final Character DECLARATION_DELIMITER = ';';
    private static final String ASTERISK_DECLARATION = ".*;";

    private ExploreContext exploreCtx;
    private Project project;
    private Document document;
    private CharSequence editorText;
    private int caret;

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("API Exploring triggered.");
        exploreCtx = buildExploreContext(e);

        Editor editor = e.getRequiredData(EDITOR);
        project = e.getRequiredData(PROJECT);
        document = editor.getDocument();
        editorText = document.getText();
        caret = editor.getCaretModel().getOffset();

        runWriteCommandAction(project, generateAPI());
    }

    private ExploreContext buildExploreContext(AnActionEvent e) {
        return new ExploreContext();
    }

    private Runnable generateAPI() {
        return () -> {
            try {
                exploreCtx.setUserInput(defineUserInput());
                exploreCtx.setIndent(getTabsNumber(document, caret));
                System.out.printf("User input [%s] was defined as [%s] with indent size=[%d]%n",
                        exploreCtx.getUserInput(), exploreCtx.getInputType().name(), exploreCtx.getIndent().length());

                final String className = getClassName(exploreCtx);
                System.out.printf("Necessary Class name: [%s]%n", className);
                final List<String> importList = getImportList();
                System.out.printf("Imports: %s%n", importList);

                final Optional<Class<?>> maybeClass = findClassByName(getFullClassName(importList, className));
                Class<?> clazz = maybeClass.isPresent()
                        ? maybeClass.get()
                        : findClassByNameList(getClassPathApplicants(importList), className)
                        .orElseThrow(() -> new NoImportException("Import for '" + className + "' class not found. Declare import first"));
                System.out.printf("Defined Class: [%s]%n", clazz);
                exploreCtx.buildApi(clazz);

                String generatedStr = generateApiString(exploreCtx);
                updateEditor(generatedStr, exploreCtx);

            } catch (UnknownInputException | NoImportException | NoClassException e) {
                showMessageDialog(project, e.getMessage(), "Error", getErrorIcon());
            } catch (NotImplementedException e) {
                showMessageDialog(project, e.getMessage(), "Info", getInformationIcon());
            }
        };
    }

    private String generateApiString(ExploreContext ctx) {
        System.out.printf("Generating %d methods for %s with %d spaces each%n", ctx.getAPI().size(), ctx.getUserInput(), ctx.getIndent().length());

        StringBuilder sb = new StringBuilder();
        ctx.getAPI().stream()
                .map(Method::getName)
                .forEach(methodName -> sb
                        .append(ctx.getUserInput())
                        .append(".")
                        .append(methodName)
                        .append("(")
                        .append(");")
                        .append("\n")
                        .append(ctx.getIndent()));
        return sb.toString();
    }

    private void updateEditor(String generatedCode, ExploreContext ctx) {
        document.replaceString(caret - ctx.getUserInput().length() - 1, caret, generatedCode);
    }

    private List<String> getClassPathApplicants(List<String> importList) {
        System.out.println("Collecting asterisk declarations...");
        List<String> classPathApplicants = importList.stream()
                .filter(s -> s.endsWith(ASTERISK_DECLARATION))
                .map(s -> s.substring(IMPORT_STRING_PREFIX.length(), s.length() - ASTERISK_DECLARATION.length()))
                .collect(toList());

        System.out.println(classPathApplicants.isEmpty()
                ? "No asterisk declarations in imports"
                : format("Path applicants: [%s]", classPathApplicants));
        return classPathApplicants;
    }

    private String getFullClassName(List<String> importList, String className) {
        String fullClassName = importList.stream()
                .filter(importStr -> {
                    final boolean hasClassName = importStr.endsWith(className + DECLARATION_DELIMITER);
                    System.out.println(hasClassName
                            ? format("[%s] has class name", importStr)
                            : format("[%s] doesn't fit", importStr));
                    return hasClassName;
                })
                .filter(importStr -> {
                    final boolean isFit = PACKAGE_DELIMITER == importStr.charAt(importStr.length() - className.length() - 2);
                    System.out.println(isFit
                            ? format("[%s] exactly fits!", importStr)
                            : format("[%s] doesn't exactly fit", importStr));
                    return isFit;
                })
                .map(s -> s.substring(IMPORT_STRING_PREFIX.length(), s.length() - 1))
                .findFirst()
                .orElse("");
        System.out.println(fullClassName.isBlank()
                ? "Necessary Class was not found in imports"
                : format("Full Class name: [%s]", fullClassName));
        return fullClassName;
    }

    private List<String> getImportList() {
        final ArrayList<String> importList = new ArrayList<>();
        System.out.println("Scanning imports to define Full Class name...");
        Scanner scanner = new Scanner(editorText.toString()).useDelimiter("\n");
        while (scanner.hasNext()) {
            String line = scanner.next();
            if (line.startsWith(IMPORT_STRING_PREFIX)) {
                importList.add(line);
            }
        }
        return importList;
    }

    private String defineUserInput() {
        final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        final int lastElementOffset = caret - 2;
        String userInput = "";

        if (psiFile != null) {
            PsiElement elementAtCaret = psiFile.findElementAt(lastElementOffset);
            if (elementAtCaret != null) {
                userInput = elementAtCaret.getText();
            }
        }
        return userInput;
    }

    private String getClassName(ExploreContext exploreContext) throws NotImplementedException, UnknownInputException {
        switch (exploreContext.getInputType()) {
            case TYPE: return exploreContext.getUserInput();
            case OBJECT: return defineClassFromObject(exploreContext);
            default: throw new UnknownInputException("Caret should stay on exploring object or type");
        }
    }

    private String defineClassFromObject(ExploreContext exploreContext) throws NotImplementedException {
        System.out.printf("Trying to define object [%s]...%n", exploreContext.getUserInput());
        // TODO: 28.02.2024 find place object was init
        // TODO: 28.02.2024 return type of this expression
        throw new NotImplementedException("Objects defining is not implemented in current version");
    }

    private Optional<Class<?>> findClassByName(String className) throws NoClassException {
        if (className.isBlank()) {
            return empty();
        } else {
            System.out.printf("Trying find Class by name: [%s]...%n", className);
            try {
                return of(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new NoClassException("Class " + className + " not found in classpath");
            }
        }
    }

    private Optional<Class<?>> findClassByNameList(List<String> classNameList, String className) {
        if (!classNameList.isEmpty()) {
            for (String path : classNameList) {
                try {
                    String fullClassName = path + PACKAGE_DELIMITER + className;
                    System.out.printf("Trying find Class by name: [%s]...%n", fullClassName);
                    return of(Class.forName(fullClassName));
                } catch (ClassNotFoundException ex) {
                    System.out.println("No such file...");
                    // just try next applicant
                }
            }
        }
        return empty();
    }

    private int getTabsNumber(Document document, int caretOffset) {
        System.out.println("Defining indent...");

        final int lineNumber = document.getLineNumber(caretOffset);
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final int lineEndOffset = document.getLineEndOffset(lineNumber);

        final String line = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        System.out.printf("Captured line %d: [%s]%n", lineNumber, line);

        int indentCount = 0;
        for (int i = 0; i < caretOffset && i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                indentCount++;
            }
        }
        System.out.printf("Defined %s spaces%n", indentCount);
        return indentCount;
    }
}

// TODO: 28.02.2024 fix standard library search
// TODO: 27.02.2024 Generate compile-safe code for virtual methods(for class/obj input)
// TODO: 27.02.2024 Generate checkers(return boolean)
// TODO: 27.02.2024 Generate getters(startsWith "get..")
// TODO: 27.02.2024 Generate setters(startsWith "set..")
// TODO: 27.02.2024 Generate all methods(incl.static,incl.parent, incl. deprecated)
// TODO: 27.02.2024 Generate all methods with overload & some params
// TODO: 27.02.2024 Generate all methods with overload, params & return var
// TODO: 27.02.2024 Generate all method tree(depth) with default params
// TODO: 27.02.2024 Handle potential errors (e.g., invalid PSI elements)
// TODO: 27.02.2024 Provide options for customizing default parameters
// TODO: 27.02.2024 Provide options for method filtering
// TODO: 27.02.2024 Test the plugin thoroughly in different scenarios
// TODO: 27.02.2024 Consider using a templating library for more complex code generation. Register the action with a custom live template
