package ui;

import GUIController.Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Padcode {
    private Scene scene;
    private VBox outerBox;
    private TreeView<?> explorerView;
    private TabPane tabPane;
    private HBox bottomBox;
    private Controller controller = new Controller();

    // initialize
    public Padcode() {
        // #region Top
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open File...");
        MenuItem openFolder = new MenuItem("Open Folder...");
        SeparatorMenuItem sep1 = new SeparatorMenuItem();
        MenuItem saveFile = new MenuItem("Save");
        MenuItem saveAsFile = new MenuItem("Save As...");
        SeparatorMenuItem sep2 = new SeparatorMenuItem();
        MenuItem exit = new MenuItem("Exit");

        Menu editMenu = new Menu("Edit");
        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        SeparatorMenuItem sep3 = new SeparatorMenuItem();
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");

        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");

        VBox.setVgrow(menuBar, Priority.NEVER);
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        fileMenu.getItems().addAll(newFile, openFile, openFolder, sep1, saveFile, saveAsFile, sep2, exit);
        editMenu.getItems().addAll(undo, redo, sep3, cut, copy, paste);
        helpMenu.getItems().add(about);
        // #endregion

        // #region Mid
        explorerView = new TreeView<>();
        SplitPane.setResizableWithParent(explorerView, false);
        tabPane = new TabPane(Controller.getNewTab("Uncoded"));
        tabPane.setOnMouseClicked(controller::createNewTab);
        tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        SplitPane splitPane = new SplitPane(explorerView, tabPane);
        splitPane.setDividerPosition(0, 0.25);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        controller.setExplorerView(explorerView);
        controller.setTabPane(tabPane);
        // #endregion

        //#endregion Bottom
        bottomBox = new HBox(5, new Label("Status"));
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(3, 3, 3, 3)); // t r b l
        VBox.setVgrow(bottomBox, Priority.NEVER);
        //#region

        outerBox = new VBox(menuBar, splitPane, bottomBox);
        outerBox.setPrefSize(900, 600); // w h

        controller.initialize();

        scene = new Scene(outerBox);
    }

    public Scene getScene() {
        return scene;
    }

}
