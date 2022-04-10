package ui.MyException;

public class FileIsNotTxtException extends Exception {
    public FileIsNotTxtException() {
        super("This file is not txt file");
    }
}
