package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.joymutlu.apiexplorer.model.UserInput;

public final class InputInitService {
    private final String line;
    private final int lineOffset;
    private final int caretOffset;

    public InputInitService(Document document, Caret caret) {
        caretOffset = caret.getOffset();
        final int lineNumber = document.getLineNumber(caretOffset);
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final int lineEndOffset = document.getLineEndOffset(lineNumber);
        line = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        lineOffset = caretOffset - lineStartOffset;
    }

    public UserInput initUserInput() {
        System.out.println("Defining user input...");
        int leftIdx = lineOffset - 1;
        int rightIdx = lineOffset;
        while (leftIdx >= 0 && line.charAt(leftIdx) != ' ') {
            leftIdx--;
        }
        leftIdx++;
        while (rightIdx < line.length() && line.charAt(rightIdx) != ' ') {
            rightIdx++;
        }
        int leftStep = lineOffset - leftIdx;
        int rightStep = rightIdx - lineOffset;
        final String[] fullInput = line.substring(leftIdx, rightIdx).split("\\.");

        String filter = fullInput.length == 2 ? fullInput[1] : "";
        int indentNumber = lineOffset - leftStep;
        return new UserInput(fullInput[0], indentNumber, filter, caretOffset - leftStep, caretOffset + rightStep);
    }
}
