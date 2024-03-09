package com.joymutlu.apiexplorer.service;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.joymutlu.apiexplorer.exception.NoInitializingLineException;
import com.joymutlu.apiexplorer.exception.UnknownInputException;
import com.joymutlu.apiexplorer.model.ExploreContext;
import com.joymutlu.apiexplorer.model.UserInputInfo;
import com.joymutlu.apiexplorer.util.EditorConstants;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class EditorService {
    private final Editor editor;
    private final Document document;
    private final String editorText;
    private final Caret primaryCaret;
    private final int caretOffset;
    private final int lineNumber;
    private final String line;
    private final int lineOffset;
    private final List<String> importList;
    private final ClassSearchService classSearchService;

    public EditorService(Editor editor) {
        this.editor = editor;
        document = editor.getDocument();
        editorText = document.getText();
        primaryCaret = editor.getCaretModel().getPrimaryCaret();
        caretOffset = primaryCaret.getOffset();
        lineNumber = document.getLineNumber(caretOffset);
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final int lineEndOffset = document.getLineEndOffset(lineNumber);
        line = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        lineOffset = caretOffset - lineStartOffset;
        importList = Arrays.stream(editorText.split("\n"))
                .filter(line -> line.startsWith(EditorConstants.IMPORT_STRING_PREFIX))
                .collect(toList());
        classSearchService = new ClassSearchService(editorText);
    }

    public UserInputInfo findUserInput() {
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
        return new UserInputInfo(fullInput[0], filter, caretOffset - leftStep, caretOffset + rightStep, leftStep);
    }

    public String findClassName(ExploreContext ctx) throws UnknownInputException, NoInitializingLineException {
        switch (ctx.getUserInput().getInputType()) {
            case TYPE: return ctx.getUserInput().value();
            case OBJECT: return classSearchService.findClassNameByObject(ctx);
            default: throw new UnknownInputException();
        }
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

    public Caret getPrimaryCaret() {
        return primaryCaret;
    }

    public int getCaretOffset() {
        return caretOffset;
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
