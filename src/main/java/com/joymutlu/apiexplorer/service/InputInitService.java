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
        final int leftIdx = getLeftInputIdx();
        final int rightIdx = getRightInputIdx();
        final int leftStep = lineOffset - leftIdx;
        final int rightStep = rightIdx - lineOffset;
        String fullInput = line.substring(leftIdx, rightIdx);
        System.out.println("Full Input=" + fullInput);

        String[] inputElements = fullInput.split("[.;]");
        String filter = "";
        if (hasFilter(inputElements)) {
            filter = inputElements[inputElements.length - 1];
            inputElements = stripLastElement(inputElements);
        }

        final String value = join(".", inputElements);
        int indentNumber = getSpaceNumber();
        System.out.println("Input=" + value);
        System.out.println("Filter=" + filter);
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

    private boolean hasFilter(String[] inputElements) {
        return inputElements.length > 1 && endsWithLetter(inputElements[inputElements.length - 1]);
    }

    private String[] stripLastElement(String[] elements) {
        if (elements == null || elements.length == 0) {
            return elements;
        }
        return Arrays.copyOf(elements, elements.length - 1);
    }

    private int getLeftInputIdx() {
        int leftIdx = lineOffset - 1;
        while (leftIdx >= 0 && !isInputStart(leftIdx)) {
            leftIdx--;
        }
        leftIdx++;
        return leftIdx;
    }

    private int getRightInputIdx() {
        int rightIdx = lineOffset;
        while (rightIdx < line.length() && !isInputEnd(rightIdx)) {
            rightIdx++;
        }
        return rightIdx;
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
        return line.charAt(idx) == ' ';
    }

    private boolean isInputStart(int idx) {
        return line.charAt(idx) == ' ';
    }
}
