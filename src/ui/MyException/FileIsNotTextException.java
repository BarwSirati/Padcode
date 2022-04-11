package ui.MyException;

public class FileIsNotTextException extends Exception {
    public FileIsNotTextException() {
        super("This file is not text file");
    }
}
