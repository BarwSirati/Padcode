package ui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.MyException.FileIsDirectoryException;
import ui.MyException.FileIsNotTextException;

public class NoteTab extends Tab {
    private static int spaces = 4;
    TextArea note = createTextArea();
    File file;

    public NoteTab() {
        super("Uncoded");
        setContent(note);
    }

    public NoteTab(String name) {
        super(name);
        setContent(note);
    }

    public NoteTab(File file) throws FileIsDirectoryException, FileIsNotTextException {
        super(file.getName());
        setContent(note);
        setFile(file);
    }

    public void setFile(File file) throws FileIsDirectoryException, FileIsNotTextException {
        this.file = file;
        if (file.isDirectory()) {
            throw new FileIsDirectoryException();
        }
        if (!isTextFile(file)) {
            throw new FileIsNotTextException();
        }
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println(e);
            text = e.toString();
        }
        note.setText(text);
        super.setText(file.getName());
    }

    public void setFileWithoutCheck(File file) {
        this.file = file;
    }

    private boolean isTextFile(File file) {
        try {
            String type = Files.probeContentType(file.toPath());
            if (type == null) {
                return false;
            }
            return type.startsWith("text");
        } catch (IOException e) {
            return false;
        }
    }

    // For TextArea, change '\t' to 4 spaces
    static public TextFormatter<?> getTabFormat() {
        return new TextFormatter<>(change -> {
            if (change.getText().contains("\t")) {
                change.setText(change.getText().replace("\t", " ".repeat(spaces)));
                int pos = Math.min(change.getCaretPosition() + 3, change.getControlNewText().length());
                change.setCaretPosition(pos);
                change.selectRange(pos, pos);
            }
            return change;
        });
    }

    static public TextArea createTextArea() {
        TextArea temp = new TextArea();
        temp.setTextFormatter(getTabFormat());
        temp.setFont(Font.font("Consolas", FontWeight.NORMAL, 16));
        return temp;
    }

    public TextArea getNote() {
        return note;
    }

    public File getFile() {
        return file;
    }
}
