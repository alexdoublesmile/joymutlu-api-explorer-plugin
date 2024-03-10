package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.util.EditorConstants;

import java.util.Arrays;
import java.util.List;

import static com.joymutlu.apiexplorer.util.EditorConstants.NEW_LINE;
import static com.joymutlu.apiexplorer.util.EditorConstants.PACKAGE_STRING_PREFIX;
import static java.util.stream.Collectors.toList;

public final class UserInputService {
    private final Editor editor;
    private final ClassNameSearchService classNameSearchService;
    private final InputInitService inputInitService;
    private final UserInput userInput;

    public UserInputService(Editor editor) {
        this.editor = editor;
        classNameSearchService = new ClassNameSearchService(editor.getDocument().getText());
        inputInitService = new InputInitService(editor.getDocument(), editor.getCaretModel().getPrimaryCaret());
        userInput = inputInitService.initUserInput();
    }

    public String findClassName() throws NoInitializingLineException, UnknownInputException {
        switch (userInput.getType()) {
            case TYPE: return userInput.getValue();
            case OBJECT: return classNameSearchService.findClassNameByObject(userInput.getValue());
            default: throw new UnknownInputException();
        }
    }

    public Document getDocument() {
        return editor.getDocument();
    }

    public List<String> getImportList() {
        return Arrays.stream(editor.getDocument().getText().split("\n"))
                .filter(line -> line.startsWith(EditorConstants.IMPORT_STRING_PREFIX))
                .collect(toList());
    }

    public UserInput getUserInput() {
        return userInput;
    }

    public String getCurrentPackage() {
        final String editorText = editor.getDocument().getText();
        return editorText.substring(
                PACKAGE_STRING_PREFIX.length(),
                editorText.indexOf(NEW_LINE) - 1);
    }
}
