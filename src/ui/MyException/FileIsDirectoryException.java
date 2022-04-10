package ui.MyException;

public class FileIsDirectoryException extends Exception {
    public FileIsDirectoryException() {
        super("This is Folder");
    }
}
