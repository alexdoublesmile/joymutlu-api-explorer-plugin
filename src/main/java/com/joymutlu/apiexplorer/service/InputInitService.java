package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.joymutlu.apiexplorer.model.UserInput;

import java.util.Arrays;

import static com.joymutlu.apiexplorer.util.EditorConstants.SPACE;
import static java.lang.String.join;

public final class InputInitService {
    private final String line;
    private final int lineOffset;
    private final int lineNumber;
    private final int caretOffset;

    public InputInitService(Document document, Caret caret) {
        caretOffset = caret.getOffset();
        lineNumber = document.getLineNumber(caretOffset);
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final int lineEndOffset = document.getLineEndOffset(lineNumber);
        line = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        lineOffset = caretOffset - lineStartOffset;
    }

    public UserInput initUserInput() {
        System.out.println("Defining user input...");
        int leftIdx = lineOffset - 1;
        int rightIdx = lineOffset;
        while (leftIdx >= 0 && !isInputStart(leftIdx)) {
            leftIdx--;
        }
        leftIdx++;
        while (rightIdx < line.length() && !isInputEnd(rightIdx)) {
            rightIdx++;
        }
        int leftStep = lineOffset - leftIdx;
        int rightStep = rightIdx - lineOffset;
        final String[] fullInput = line.substring(leftIdx, rightIdx).split("\\.");
        System.out.println("Full input=" + Arrays.toString(fullInput));

        String filter = "";
        if (fullInput.length > 1 && endsWithLetter(fullInput[fullInput.length - 1])) {
            filter =  fullInput[fullInput.length - 1];
            fullInput[fullInput.length - 1] = "";
        }
        final String value = join(".", fullInput);
        int indentNumber = getSpaceNumber();
        System.out.println("Input=" + value);
        System.out.println("Indent=" + indentNumber);
        return new UserInput(
                value,
                indentNumber,
                filter,
                lineNumber,
                caretOffset - leftStep,
                caretOffset + rightStep
        );
    }

    private boolean endsWithLetter(String lastInput) {
        return Character.isAlphabetic(lastInput.charAt(lastInput.length() - 1));
    }

    private int getSpaceNumber() {
        int result = 0;
        while (line.charAt(result) == SPACE) {
            result++;
        }
        return result;
    }

    private boolean isInputEnd(int idx) {
        return line.charAt(idx) == ' '
                || line.charAt(idx) == ';';
    }

    private boolean isInputStart(int idx) {
        return line.charAt(idx) == ' ';
    }
}
