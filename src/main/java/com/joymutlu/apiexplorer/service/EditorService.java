package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.joymutlu.apiexplorer.model.UserInput;
import com.joymutlu.apiexplorer.util.EditorConstants;

import java.util.Arrays;
import java.util.List;

public final class EditorService {
    private final Editor editor;
    private final Document document;
    private final String editorText;
    private final int caret;
    private final int lineNumber;
    private final String line;
    private final int lineOffset;
    private final List<String> importList;

    public EditorService(Editor editor) {
        this.editor = editor;
        document = editor.getDocument();
        editorText = document.getText();
        caret = editor.getCaretModel().getOffset();
        lineNumber = document.getLineNumber(caret);
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final int lineEndOffset = document.getLineEndOffset(lineNumber);
        line = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        lineOffset = caret - lineStartOffset;
        importList = Arrays.stream(editorText.split("\n"))
                .filter(line -> line.startsWith(EditorConstants.IMPORT_STRING_PREFIX))
                .toList();
    }

    public UserInput defineUserInput() {
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
        if (line.charAt(rightIdx - 1) == '.') {
            rightIdx--;
        }
        return new UserInput(
                line.substring(leftIdx, rightIdx),
                caret - leftStep,
                caret + rightStep,
                leftStep);
    }

    public int getLineOffset() {
        return lineOffset;
    }

    public String getEditorText() {
        return editorText;
    }

    public Editor getEditor() {
        return editor;
    }

    public Document getDocument() {
        return document;
    }

    public int getCaret() {
        return caret;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLine() {
        return line;
    }

    public List<String> getImportList() {
        return importList;
    }
}
