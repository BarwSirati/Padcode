package GUIController;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Controller {

    @FXML
    private TreeView<?> explorerView;

    @FXML
    private TabPane tabPane;

    @FXML
    public void initialize() {
        System.out.println("start");
        for (var tab : tabPane.getTabs()) {
            ((TextArea) tab.getContent()).setTextFormatter(getTabFormat());
        }
    }

    // For TextArea, change '\t' to 4 spaces
    static public TextFormatter<?> getTabFormat() {
        return new TextFormatter<>(change -> {
            if (change.getText().contains("\t")) {
                change.setText(change.getText().replace("\t", "    "));
                int pos = Math.min(change.getCaretPosition() + 3, change.getControlNewText().length());
                change.setCaretPosition(pos);
                change.selectRange(pos, pos);
            }
            return change;
        });
    }

    // Create new tab object
    static public Tab getNewTab(String name) {
        TextArea textArea = new TextArea();
        Tab txtTab = new Tab(name, textArea);
        textArea.setTextFormatter(getTabFormat());
        textArea.setFont(Font.font("Consolas", FontWeight.NORMAL, 16));
        return txtTab;
    }

    @FXML // Double click on tabpane to create new tab
    public void createNewTab(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount() == 2){
                Tab t = getNewTab("Uncoded");
                tabPane.getTabs().add(t);
                tabPane.getSelectionModel().select(t);
            }
        }
    }

    public TreeView<?> getExplorerView() {
        return explorerView;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public void setExplorerView(TreeView<?> explorerView) {
        this.explorerView = explorerView;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }
}
