package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.editor.Editor;
import com.joymutlu.apiexplorer.model.UserInput;

public final class UserInputService {
    private final Editor editor;
    private final InputInitService inputInitService;
    private final UserInput userInput;

    public UserInputService(Editor editor) {
        this.editor = editor;
        inputInitService = new InputInitService(editor.getDocument(), editor.getCaretModel().getPrimaryCaret());
        userInput = inputInitService.initUserInput();
    }

    public UserInput getUserInput() {
        return userInput;
    }
}
