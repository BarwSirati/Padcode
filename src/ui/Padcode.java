package ui;

import java.util.function.Consumer;
import java.util.prefs.Preferences;

import GUIController.Controller;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private Preferences pref = Preferences.userNodeForPackage(Padcode.class); 

    // initialize
    public Padcode() {
        // #region Top
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newFile.setOnAction(e -> {
            var t = controller.tabSetOnCloseRequest(new NoteTab());
            t.setModified(false);
            tabPane.getTabs().add(t);
            tabPane.getSelectionModel().select(t);
        });
        MenuItem openFile = new MenuItem("Open File...");
        openFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openFile.setOnAction(controller::menuOpenFile);
        MenuItem openFolder = new MenuItem("Open Folder...");
        openFolder.setAccelerator(
                new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        openFolder.setOnAction(controller::menuOpenFolder);
        SeparatorMenuItem sep1 = new SeparatorMenuItem();
        MenuItem saveFile = new MenuItem("Save");
        saveFile.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveFile.setOnAction(controller::menuSave);
        MenuItem saveAsFile = new MenuItem("Save As...");
        saveAsFile.setAccelerator(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        saveAsFile.setOnAction(controller::menuSaveAs);
        SeparatorMenuItem sep2 = new SeparatorMenuItem();
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> Platform.exit());

        Menu editMenu = new Menu("Edit");
        MenuItem undo = new MenuItem("Undo");
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        undo.setOnAction(e -> currentTextArea(TextArea::undo));

        MenuItem redo = new MenuItem("Redo");
        redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        redo.setOnAction(e -> currentTextArea(TextArea::redo));

        SeparatorMenuItem sep3 = new SeparatorMenuItem();
        MenuItem cut = new MenuItem("Cut");
        cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        cut.setOnAction(e -> currentTextArea(TextArea::cut));

        MenuItem copy = new MenuItem("Copy");
        copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        copy.setOnAction(e -> currentTextArea(TextArea::copy));

        MenuItem paste = new MenuItem("Paste");
        paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        paste.setOnAction(e -> currentTextArea(TextArea::paste));
        SeparatorMenuItem sep4 = new SeparatorMenuItem();
        MenuItem font = new MenuItem("Font");
        font.setOnAction(controller::menuFont);

        Menu themeMenu = new Menu("Theme");

        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("No");

        VBox.setVgrow(menuBar, Priority.NEVER);
        menuBar.getMenus().addAll(fileMenu, editMenu, themeMenu, helpMenu);
        fileMenu.getItems().addAll(newFile, openFile, openFolder, sep1, saveFile, saveAsFile, sep2, exit);
        editMenu.getItems().addAll(undo, redo, sep3, cut, copy, paste, sep4, font);
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
        SplitPane splitPane = new SplitPane(tabPane);
        splitPane.setDividerPosition(0, 0);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        // #endregion

        // #region Bottom
        Label status = new Label("Status");
        status.setId("status");
        bottomBox = new HBox(5, status);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(3, 3, 3, 3)); // t r b l
        bottomBox.setId("HBox");
        VBox.setVgrow(bottomBox, Priority.NEVER);
        // #endregion

        outerBox = new VBox(menuBar, splitPane, bottomBox);
        outerBox.setPrefSize(900, 600); // w h

        scene = new Scene(outerBox);

        String theme = pref.get("theme","Winter");

        ToggleGroup tg = new ToggleGroup();
        RadioMenuItem modena = new RadioMenuItem("Modena");
        modena.setOnAction(e -> scene.getStylesheets().clear());
        modena.setToggleGroup(tg);
        themeMenu.getItems().add(modena);

        RadioMenuItem dark = createThemeItem(themeMenu, "Dark", tg);
        RadioMenuItem darkPink = createThemeItem(themeMenu, "DarkPink", tg);
        RadioMenuItem grey = createThemeItem(themeMenu, "Grey", tg);
        RadioMenuItem winter = createThemeItem(themeMenu, "Winter", tg);

        if (theme.equals("Normal")) {
            modena.setSelected(true);
        } else if (theme.equals("Dark")) {
            dark.setSelected(true);
        } else if (theme.equals("DarkPink")) {
            darkPink.setSelected(true);
        } else if (theme.equals("Grey")) {
            grey.setSelected(true);
        } else if (theme.equals("Winter")) {
            winter.setSelected(true);
        }

        controller.setExplorerView(explorerView);
        controller.setTabPane(tabPane);
        controller.setSplitPane(splitPane);
        controller.initialize();
    }

    public Scene getScene() {
        return scene;
    }

    private void currentTextArea(Consumer<TextArea> action) {
        Tab t = tabPane.getSelectionModel().getSelectedItem();
        if (t != null && t.getContent() instanceof TextArea ta) {
            action.accept(ta);
        }
    }

    private RadioMenuItem createThemeItem(Menu themeMenu, String name, ToggleGroup tg) {
        RadioMenuItem themeItem = new RadioMenuItem(name);
        themeItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ob, Boolean old_val, Boolean new_val) {
                if (!old_val && new_val) {
                    pref.put("theme",name);
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(
                            getClass().getClassLoader().getResource("./asset/Theme/" + name + ".css").toString());
                }
            }
        });
        themeItem.setToggleGroup(tg);
        themeMenu.getItems().add(themeItem);
        return themeItem;
    }
}
