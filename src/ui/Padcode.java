package ui;

import GUIController.Controller;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
    public Controller controller = new Controller();

    // initialize
    public Padcode() {
        // #region Top
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newFile.setOnAction(e -> {
            var t = controller.tabSetOnCloseRequest(new NoteTab());
            tabPane.getTabs().add(t);
            tabPane.getSelectionModel().select(t);
        });
        MenuItem openFile = new MenuItem("Open File...");
        openFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openFile.setOnAction(controller::menuOpenFile);
        MenuItem openFolder = new MenuItem("Open Folder...");
        openFolder.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        openFolder.setOnAction(controller::menuOpenFolder);
        SeparatorMenuItem sep1 = new SeparatorMenuItem();
        MenuItem saveFile = new MenuItem("Save");
        saveFile.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveFile.setOnAction(controller::menuSave);
        MenuItem saveAsFile = new MenuItem("Save As...");
        saveAsFile.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        saveAsFile.setOnAction(controller::menuSaveAs);
        SeparatorMenuItem sep2 = new SeparatorMenuItem();
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> Platform.exit());
        
        Menu editMenu = new Menu("Edit");
        MenuItem undo = new MenuItem("Undo");
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        undo.setOnAction(e -> {
            Tab t = tabPane.getSelectionModel().getSelectedItem();
            if (t != null && t.getContent() instanceof TextArea ta) {
                ta.undo();
            }
        });

        MenuItem redo = new MenuItem("Redo");
        redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        redo.setOnAction(e -> {
            Tab t = tabPane.getSelectionModel().getSelectedItem();
            if (t != null && t.getContent() instanceof TextArea ta) {
                ta.redo();
            }
        });

        SeparatorMenuItem sep3 = new SeparatorMenuItem();
        MenuItem cut = new MenuItem("Cut");
        cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        cut.setOnAction(e -> {
            Tab t = tabPane.getSelectionModel().getSelectedItem();
            if (t != null && t.getContent() instanceof TextArea ta) {
                ta.cut();
            }
        });

        MenuItem copy = new MenuItem("Copy");
        copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        copy.setOnAction(e -> {
            Tab t = tabPane.getSelectionModel().getSelectedItem();
            if (t != null && t.getContent() instanceof TextArea ta) {
                ta.copy();
            }
        });

        MenuItem paste = new MenuItem("Paste");
        paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        paste.setOnAction(e -> {
            Tab t = tabPane.getSelectionModel().getSelectedItem();
            if (t != null && t.getContent() instanceof TextArea ta) {
                ta.paste();
            }
        });
        SeparatorMenuItem sep4 = new SeparatorMenuItem();
        MenuItem font = new MenuItem("Font");
        font.setOnAction(controller::menuFont);

        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("No");

        VBox.setVgrow(menuBar, Priority.NEVER);
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        fileMenu.getItems().addAll(newFile, openFile, openFolder, sep1, saveFile, saveAsFile, sep2, exit);
        editMenu.getItems().addAll(undo, redo, sep3, cut, copy, paste,sep4,font);
        helpMenu.getItems().add(about);
        // #endregion

        // #region Mid
        explorerView = new TreeView<>();
        explorerView.setOnMouseClicked(controller::selectItem);
        SplitPane.setResizableWithParent(explorerView, false);
        tabPane = new TabPane(controller.tabSetOnCloseRequest(new NoteTab()));
        tabPane.setOnMouseClicked(controller::createNewTab);
        tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        tabPane.setTabDragPolicy(TabDragPolicy.REORDER);
        SplitPane splitPane = new SplitPane(explorerView, tabPane);
        splitPane.setDividerPosition(0, 0);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        controller.setExplorerView(explorerView);
        controller.setTabPane(tabPane);
        controller.setSplitPane(splitPane);
        // #endregion

        //#endregion Bottom
        Label status = new Label("Status");
        status.setId("status");
        bottomBox = new HBox(5, status);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(3, 3, 3, 3)); // t r b l
        bottomBox.setId("HBox");
        VBox.setVgrow(bottomBox, Priority.NEVER);
        //#region

        outerBox = new VBox(menuBar, splitPane, bottomBox);
        outerBox.setPrefSize(900, 600); // w h

        scene = new Scene(outerBox);

        controller.initialize();
    }

    public Scene getScene() {
        return scene;
    }

}
