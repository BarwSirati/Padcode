package ui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.MyException.FileIsDirectoryException;
import ui.MyException.FileIsNotTextException;

public class NoteTab extends Tab {
    private static int spaces = 4;
    TextArea note = createTextArea();
    File file;
    boolean modified = false;
    public static Font font = Font.font("Consolas", FontWeight.NORMAL, 16);

    public NoteTab(String name) {
        super(name);
        setContent(note);
        note.textProperty().addListener((ob, o, n) -> {
            setModified(true);
        });
    }

    public NoteTab() {
        this("Uncoded");
    }

    public NoteTab(File file) throws FileIsDirectoryException, FileIsNotTextException {
        super(file.getName());
        setContent(note);
        setFile(file);
        note.textProperty().addListener((ob, o, n) -> {
            setModified(true);
        });
    }

    public void setFile(File file) throws FileIsDirectoryException, FileIsNotTextException {
        this.file = file;
        if (file.isDirectory()) {
            throw new FileIsDirectoryException();
        }
        super.setText(file.getName());
        if (isImageFile(file)) {
            Image img = new Image(file.getPath());
            ImageView imgView = new ImageView(img);
            imgView.setPreserveRatio(true);
            imgView.setFitHeight(img.getHeight());
            VBox box = new VBox(imgView);
            box.setAlignment(Pos.CENTER);
            setContent(box);
            imgView.setOnScroll(e -> {
                if (imgView.getFitHeight() + e.getDeltaY() > 0) {
                    imgView.setFitHeight(imgView.getFitHeight() + e.getDeltaY());
                }
            });
            return;
        }
        if (!isTextFile(file)) {
            throw new FileIsNotTextException();
        }
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            text = "";
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("File Not Found");
            alert.setContentText(file.getName() + " cannot be found.");
            alert.initOwner(this.getTabPane().getScene().getWindow());
            alert.show();
        } catch (IOException e) {
            System.out.println(e);
            text = e.toString();
        }
        note.setText(text);
    }

    public void setFileWithoutCheck(File file) {
        this.file = file;
    }

    private static boolean isTextFile(File file) {
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

    private static boolean isImageFile(File file) {
        try {
            String type = Files.probeContentType(file.toPath());
            if (type == null) {
                return false;
            }
            return type.startsWith("image");
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
        temp.setFont(font);
        return temp;
    }

    public TextArea getNote() {
        return note;
    }

    public File getFile() {
        return file;
    }

    public boolean wasModified() {
        return modified;
    }

    public void setModified(boolean value) {
        if (value) { // modified: false -> true
            if (!modified) {
                modified = true;
                setText(getText() + "*");
            }
        } else { // modified: true -> false
            if (modified) {
                modified = false;
                setText(getText().replace("*", ""));
            }
        }
    }
}
