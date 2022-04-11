package GUIController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ui.FileTreeItem;
import ui.NameFile;
import ui.NoteTab;
import ui.MyException.FileIsDirectoryException;
import ui.MyException.FileIsNotTextException;

public class Controller {

    @FXML
    private TreeView<NameFile> explorerView;

    @FXML
    private TabPane tabPane;
    private SplitPane splitPane;

    FileChooser fileChooser = new FileChooser();
    DirectoryChooser dirChooser = new DirectoryChooser();
    FileChooser saveChooser = new FileChooser();
    NameFile initialDir;

    @FXML // Double click on tabpane to create new tab
    public void createNewTab(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                Tab t = new NoteTab();
                tabPane.getTabs().add(t);
                tabPane.getSelectionModel().select(t);
            }
        }
    }

    public void menuOpenFile(ActionEvent e) {
        NoteTab tab = (NoteTab) tabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            File initial = tab.getFile();
            if (initial != null) {
                fileChooser.setInitialDirectory(initial.getParentFile());
            }
        }
        List<File> list = fileChooser.showOpenMultipleDialog(null);
        if (list != null) {
            for (File file : list) {
                newTabInTabPane(file);
            }
        }
    }

    public void menuOpenFolder(ActionEvent e) {
        if (initialDir != null) {
            dirChooser.setInitialDirectory(initialDir);
        }
        File file = dirChooser.showDialog(null);
        if (file == null) {
            return;
        }
        initialDir = new NameFile(file);
        FileTreeItem root = new FileTreeItem(initialDir);
        root.setExpanded(true);
        explorerView.setRoot(root);
        if (splitPane.getDividerPositions()[0] < 0.01) {
            splitPane.setDividerPosition(0, 0.25);
        }
    }

    public void saveTextToFile(File file, TextArea content) {
        try (FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                BufferedWriter writer = new BufferedWriter(osw)) {
            writer.append(content.getText());
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public void menuSave(ActionEvent e) {
        NoteTab tab = (NoteTab) tabPane.getSelectionModel().getSelectedItem();
        if(tab.getFile().canWrite()){
            if (tab.getFile() == null) {
                menuSaveAs(e);
            } else {
                saveTextToFile(tab.getFile(), tab.getNote());
            }
        }else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("This File Is Read-Only");
            alert.setContentText("A read-only file is any file with the read-only.");
            alert.show();
        }
    }

    public void menuSaveAs(ActionEvent e) {
        saveChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
        NoteTab tab = (NoteTab) tabPane.getSelectionModel().getSelectedItem();
        File file = saveChooser.showSaveDialog(null);
        saveTextToFile(file, tab.getNote());
        tab.setText(file.getName());
        tab.setFileWithoutCheck(file);
    }

    public void setExplorerView(TreeView<NameFile> explorerView) {
        this.explorerView = explorerView;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void setSplitPane(SplitPane splitPane) {
        this.splitPane = splitPane;
    }

    public void selectItem(MouseEvent event) {
        TreeItem<NameFile> item = explorerView.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        File file = item.getValue();
        newTabInTabPane(file);
    }

    private void newTabInTabPane(File file) {
        NoteTab tab;
        try {
            tab = new NoteTab(file);
        } catch (FileIsNotTextException e1) {
            tab = new NoteTab(file.getName());
            tab.setFileWithoutCheck(file);
            tab.getNote().setText("This file is binary and cannot be displayed.");
            tab.getNote().setEditable(false);
        } catch (FileIsDirectoryException e2) {
            return;
        }
        for (Tab tab2 : tabPane.getTabs()) {
            NoteTab noteTab = (NoteTab) tab2;
            if (noteTab.getFile() != null && noteTab.getFile().equals(file)) {
                tabPane.getSelectionModel().select(tab2);
                return;
            }
        }
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
