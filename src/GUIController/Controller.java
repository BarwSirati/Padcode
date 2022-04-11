package GUIController;

import java.io.File;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ui.NoteTab;

public class Controller {

    @FXML
    private TreeView<File> explorerView;

    @FXML
    private TabPane tabPane;

    FileChooser fileChooser = new FileChooser();
    DirectoryChooser dirChooser = new DirectoryChooser();

    @FXML
    public void initialize() {
        System.out.println("start");
        for (var tab : tabPane.getTabs()) {
            ((TextArea) tab.getContent()).setTextFormatter(NoteTab.getTabFormat());
        }
    }

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
        List<File> list = fileChooser.showOpenMultipleDialog(null);
        if (list != null) {
            for (File file : list) {
                System.out.println(file);
            }
        }
    }

    public void menuOpenFolder(ActionEvent e) {
        File file = dirChooser.showDialog(null);
        System.out.print(file);
    }

    public void menuSave(ActionEvent e) {

    }

    public void menuSaveAs(ActionEvent e) {

    }

    public void setExplorerView(TreeView<File> explorerView) {
        this.explorerView = explorerView;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }
}
