package com.joymutlu.apiexplorer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.joymutlu.apiexplorer.model.ExploreConfig;
import com.joymutlu.apiexplorer.model.InputType;
import com.joymutlu.apiexplorer.strategy.ExploreStrategy;
import com.joymutlu.apiexplorer.util.ReflectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.intellij.openapi.ui.Messages.showMessageDialog;
import static com.joymutlu.apiexplorer.model.InputType.*;
import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isLowerCase;
import static java.lang.String.format;
import static java.nio.file.Files.lines;
import static java.nio.file.Path.of;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

public class ExploreClassAction extends AnAction {
    private static final String IMPORT_STRING_PREFIX = "import ";
    private static final Character PACKAGE_DELIMITER = '.';
    private static final Character DECLARATION_DELIMITER = ';';
    private static final String ASTERISK_DECLARATION = ".*;";
//    private static final Logger LOGGER = Logger.getLogger(ExploreClassAction.class.getName());

    private final Map<InputType, ExploreStrategy> exploreStrategyMap = new HashedMap();
    private ExploreConfig exploreConfig;

    @Override
    public void actionPerformed(AnActionEvent e) {
//        Handler handlerObj = new ConsoleHandler();
//        handlerObj.setLevel(Level.ALL);
//        LOGGER.addHandler(handlerObj);
//        LOGGER.setLevel(Level.ALL);
//        LOGGER.setUseParentHandlers(false);

//        LOGGER.log(INFO, "Explorer triggered.");
        System.out.println("Explorer triggered.");
        exploreConfig = new ExploreConfig();
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        final CaretModel caretModel = editor.getCaretModel();
        final int caretOffset = caretModel.getOffset();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            final Document document = editor.getDocument();
            final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            final int lastElementOffset = getLastElementOffset(caretOffset);

            PsiElement elementAtCaret;
            if ((elementAtCaret = tryDefineElement(psiFile, lastElementOffset)) != null) {
                final Path filePath = of(requireNonNull(psiFile.getVirtualFile().getCanonicalPath()));
//                LOGGER.log(INFO, format("Current file path was defined: [%s]", filePath));
                System.out.printf("Current file path was defined: [%s]%n", filePath);
                String userInput = elementAtCaret.getText();
                final InputType inputType = getInputType(userInput);
//                final ExploreStrategy exploreStrategy = exploreStrategyMap.get(inputType);
//                LOGGER.log(INFO, format("User input [%s] was defined as %s", userInput, inputType.name()));
                System.out.printf("User input [%s] was defined as %s%n", userInput, inputType.name());

                String className = userInput;
                if (inputType == OBJECT) {
//                    LOGGER.log(INFO, format("Trying to define object [%s]...", userInput));
                    System.out.printf("Trying to define object [%s]...%n", userInput);
                    // TODO: 28.02.2024 find place object was init
                    // TODO: 28.02.2024 return type of this expression
                    showMessageDialog(project,
                            "Objects defining is not implemented in current version", "Info", Messages.getInformationIcon());
                    return;
                }
                if (inputType == UNKNOWN) {
                    showMessageDialog(project,
                            "Caret should stay on exploring object or type", "Error", Messages.getErrorIcon());
                    return;
                }
//                LOGGER.log(INFO, format("Necessary Class name: [%s]...", className));
                System.out.printf("Necessary Class name: [%s]%n", className);

//                LOGGER.log(INFO, "Trying to define Full Class name...");
                System.out.println("Trying to define Full Class name...");
                String fullClassName = "";
                List<String> classPathApplicants = new ArrayList<>();
                try (final Stream<String> lines = lines(filePath)) {
//                    LOGGER.log(INFO, "Scanning imports...");
                    System.out.println("Scanning imports...");
                    final List<String> importList = lines
                            .filter(s -> s.startsWith(IMPORT_STRING_PREFIX))
                            .collect(toList());
//                    LOGGER.log(INFO, format("Imports: [%s]...", importList));
                    System.out.printf("Imports: %s%n", importList);
//                    System.out.println("Imports: " + importList);

                    fullClassName = importList.stream()
                            .filter(importStr -> {
                                final boolean hasClassName = importStr.endsWith(className + DECLARATION_DELIMITER);
//                                LOGGER.log(FINE, hasClassName
//                                        ? format("[%s] has class name!", importStr)
//                                        : format("[%s] doesn't fit...", importStr));
                                System.out.println(hasClassName
                                        ? format("[%s] has class name!", importStr)
                                        : format("[%s] doesn't fit", importStr));
                                return hasClassName;
                            })
                            .filter(importStr -> {
                                final boolean isFit = PACKAGE_DELIMITER == importStr.charAt(importStr.length() - className.length() - 2);
//                                LOGGER.log(FINE, isFit
//                                        ? format("[%s] fits!!!", importStr)
//                                        : format("[%s] doesn't fit...", importStr));
                                System.out.println(isFit
                                        ? format("[%s] fits!!!", importStr)
                                        : format("[%s] doesn't fit", importStr));
                                return isFit;
                            })
                            .map(s -> s.substring(IMPORT_STRING_PREFIX.length(), s.length() - 1))
                            .findFirst()
                            .orElse("");
//                    LOGGER.log(INFO, fullClassName.isBlank()
//                            ? "Necessary Class was not found in imports"
//                            : format("Full Class name: [%s]", fullClassName));
                    System.out.println(fullClassName.isBlank()
                            ? "Necessary Class was not found in imports"
                            : format("Full Class name: [%s]", fullClassName));

                    if (fullClassName.isBlank()) {
//                        LOGGER.log(INFO, "Collecting asterisk declarations...");
                        System.out.println("Collecting asterisk declarations...");
//                        System.out.println("Imports: " + importList);
                        classPathApplicants = importList.stream()
                                .filter(s -> s.endsWith(ASTERISK_DECLARATION))
                                .map(s -> s.substring(IMPORT_STRING_PREFIX.length(), s.length() - ASTERISK_DECLARATION.length()))
                                .collect(toList());
//                    LOGGER.log(INFO, classPathApplicants.isEmpty()
//                            ? "No asterisk declarations in imports"
//                            : format("Path applicants: [%s]", classPathApplicants));
                        System.out.println(classPathApplicants.isEmpty()
                                ? "No asterisk declarations in imports"
                                : format("Path applicants: [%s]", classPathApplicants));
                    }
                } catch (IOException ex) {
//                    LOGGER.log(SEVERE, format("Fail to parse by lines file: [%s]", filePath));
                    System.out.printf("Fail to parse by lines file: [%s]", filePath);
                    throw new RuntimeException(ex);
                }

                if (fullClassName.isBlank() && classPathApplicants.isEmpty()) {
                    showMessageDialog(project,
                            "Import for '" + className + "' not found. Declare import first", "Error", Messages.getErrorIcon());
                } else {
                    Class<?> clazz = null;
                    try {
                        if (classPathApplicants.isEmpty()) {
//                            LOGGER.log(INFO, format("Trying find Class by name: [%s]...", fullClassName));
                            System.out.printf("Trying find Class by name: [%s]...%n", fullClassName);
                            clazz = Class.forName(fullClassName);
                        } else {
                            for (String path : classPathApplicants) {
                                try {
                                    String fullApplicantPath = path + PACKAGE_DELIMITER + className;
//                                    LOGGER.log(INFO, format("Trying find Class by name: [%s]...", fullApplicantPath));
                                    System.out.printf("Trying find Class by name: [%s]...%n", fullApplicantPath);
                                    clazz = Class.forName(fullApplicantPath);
                                    break;
                                } catch (ClassNotFoundException ex) {
//                                    LOGGER.log(INFO, "No such file...");
                                    System.out.println("No such file...");
                                    // just try next path applicant
                                }
                            }
                        }
                        if (clazz == null) {
                            throw new ClassNotFoundException();
                        }
                    } catch (ClassNotFoundException ex) {
                        showMessageDialog(project,
                                "Class " + fullClassName + "was not found in classpath", "Error", Messages.getErrorIcon());
                        return;
                    }
//                    LOGGER.log(INFO, format("Defined Class: [%s]", clazz.getCanonicalName()));
                    System.out.printf("Defined Class: [%s]%n", clazz);

                    List<String> methodList = getMethodList(clazz, inputType);
                    int spacesNumber = getTabsNumber(document, caretOffset);
                    String generatedCode = getGeneratedString(userInput, getIndent(spacesNumber), methodList);
                    document.replaceString(caretOffset - userInput.length() - 1, caretOffset, generatedCode);
                }
            } else {
                showMessageDialog(project,
                        "Caret position can't be defined", "Error", Messages.getErrorIcon());
            }
//            PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        });
    }

    private String getIndent(int indentNumber) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < indentNumber; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    private int getLastElementOffset(int caretOffset) {
        return caretOffset - 2;
    }

    private @Nullable PsiElement tryDefineElement(PsiFile psiFile, int elementOffset) {
        if (psiFile != null) {
            return psiFile.findElementAt(elementOffset);
        }
        return null;
    }

    private InputType getInputType(String input) {
        char firstSymbol;
        if (!input.isBlank() && isAlphabetic(firstSymbol = input.charAt(0))) {
            if (isLowerCase(firstSymbol)) {
                return OBJECT;
            } else {
                return TYPE;
            }
        } else {
            return UNKNOWN;
        }
    }

    private List<String> getMethodList(Class<?> clazz, InputType inputType) {
        if (inputType == TYPE) {
//            LOGGER.log(INFO, "Collecting static methods...");
            if (exploreConfig.needParams()) {
                return Arrays
                        .stream(clazz.getMethods())
                        .filter(ReflectionUtils::isStatic)
                        .map(Method::getName)
                        .collect(toList());
            } else {
                System.out.println("Collecting unique public static methods...");
                return Arrays
                        .stream(clazz.getMethods())
                        .filter(ReflectionUtils::isStatic)
                        .map(Method::getName)
                        .distinct()
                        .collect(toList());
            }
        }
//        if (inputType == OBJECT) {
//            LOGGER.log(INFO, "Collecting virtual methods...");
            System.out.println("Collecting unique virtual methods...");
            return Arrays
                    .stream(clazz.getMethods())
                    .filter(ReflectionUtils::isVirtual)
                    .map(Method::getName)
                    .distinct()
                    .collect(toList());
//        }
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
//        LOGGER.log(INFO, format("Defined %s tabs", tabCount));
        System.out.printf("Defined %s spaces%n", indentCount);
        return indentCount;
    }

    private String getGeneratedString(String userInput, String indent, List<String> methods) {
        System.out.printf("Generating %d methods for %s with %d spaces each%n", methods.size(), userInput, indent.length());

        StringBuilder sb = new StringBuilder();
        for (String method : methods) {
            sb.append(userInput)
                .append(".")
                .append(method)
                .append("(")
                .append(");")
                .append("\n")
                .append(indent);
        }
        return sb.toString();
    }
}

// TODO: 28.02.2024 fix for 0 methods
// TODO: 28.02.2024 fix for import set changing
// TODO: 28.02.2024 fix standard library search
// TODO: 28.02.2024 fix for different caret position
// TODO: 28.02.2024 fix for not public methods
// TODO: 27.02.2024 Generate compile-safe code for virtual methods
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
