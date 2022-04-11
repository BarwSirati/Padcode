package ui;


import GUIController.Controller;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Padcode {
    private Scene scene;
    private VBox outerBox;
    private TreeView<NameFile> explorerView;
    private TabPane tabPane;
    private HBox bottomBox;
    private Controller controller = new Controller();

    // initialize
    public Padcode() {
        // #region Top
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        newFile.setOnAction(e -> {
            var t = new NoteTab();
            tabPane.getTabs().add(t);
            tabPane.getSelectionModel().select(t);
        });
        MenuItem openFile = new MenuItem("Open File...");
        openFile.setOnAction(controller::menuOpenFile);
        MenuItem openFolder = new MenuItem("Open Folder...");
        openFolder.setOnAction(controller::menuOpenFolder);
        SeparatorMenuItem sep1 = new SeparatorMenuItem();
        MenuItem saveFile = new MenuItem("Save");
        saveFile.setOnAction(controller::menuSave);
        MenuItem saveAsFile = new MenuItem("Save As...");
        saveAsFile.setOnAction(controller::menuSaveAs);
        SeparatorMenuItem sep2 = new SeparatorMenuItem();
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> Platform.exit());

        Menu editMenu = new Menu("Edit");
        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(e -> ((TextArea)tabPane.getSelectionModel().getSelectedItem().getContent()).undo());
        MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(e -> ((TextArea)tabPane.getSelectionModel().getSelectedItem().getContent()).redo());
        SeparatorMenuItem sep3 = new SeparatorMenuItem();
        MenuItem cut = new MenuItem("Cut");
        cut.setOnAction(e -> ((TextArea)tabPane.getSelectionModel().getSelectedItem().getContent()).cut());
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(e -> ((TextArea)tabPane.getSelectionModel().getSelectedItem().getContent()).copy());
        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(e -> ((TextArea)tabPane.getSelectionModel().getSelectedItem().getContent()).paste());

        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("No");

        VBox.setVgrow(menuBar, Priority.NEVER);
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        fileMenu.getItems().addAll(newFile, openFile, openFolder, sep1, saveFile, saveAsFile, sep2, exit);
        editMenu.getItems().addAll(undo, redo, sep3, cut, copy, paste);
        helpMenu.getItems().add(about);
        // #endregion

        // #region Mid
        explorerView = new TreeView<>();
        explorerView.setOnMouseClicked(controller::selectItem);
        SplitPane.setResizableWithParent(explorerView, false);
        tabPane = new TabPane(new NoteTab());
        tabPane.setOnMouseClicked(controller::createNewTab);
        tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        tabPane.setTabDragPolicy(TabDragPolicy.REORDER);
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

        scene = new Scene(outerBox);
    }

    public Scene getScene() {
        return scene;
    }

}
