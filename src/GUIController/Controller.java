package GUIController;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.List;
import java.util.Stack;

import javax.swing.filechooser.FileSystemView;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import ui.*;
import ui.MyException.*;
import static java.nio.file.StandardWatchEventKinds.*;
import com.sun.nio.file.ExtendedWatchEventModifier;

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
    WatchService watcher;
    Thread bgWatcher;

    public void initialize() {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @FXML // Double click on tabpane to create new tab
    public void createNewTab(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2 && !event.getTarget().toString().startsWith("TabPaneSkin")) {
                NoteTab t = new NoteTab();
                tabSetOnCloseRequest(t);
                t.setModified(false);
                tabPane.getTabs().add(t);
                tabPane.getSelectionModel().select(t);
            }
        }
    }

    public void menuFont(ActionEvent e) {
        try {
            FontSelectorDialog dialog = new FontSelectorDialog(null);
            dialog.setTitle("Font");
            dialog.showAndWait();
            var result = dialog.getResult();
            if (result == null) {
                return;
            }
            NoteTab.font = result;
            tabPane.getTabs().forEach(t -> {
                ((NoteTab) t).getNote().setFont(NoteTab.font);
            });
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void menuOpenFile(ActionEvent e) {
        NoteTab tab = (NoteTab) tabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            File initial = tab.getFile();
            if (initial != null) {
                fileChooser.setInitialDirectory(initial.getParentFile());
            } else if (initialDir != null) {
                fileChooser.setInitialDirectory(initialDir);
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
        } else {
            NoteTab tab = (NoteTab) tabPane.getSelectionModel().getSelectedItem();
            if (tab != null && tab.getFile() != null) {
                dirChooser.setInitialDirectory(tab.getFile().getParentFile());
            }
        }
        File file = dirChooser.showDialog(null);
        if (file == null) {
            return;
        }

        new Thread(new Task<Void>() {
            protected Void call() throws Exception {
                initialDir = new NameFile(file);
                FileTreeItem root = new FileTreeItem(initialDir);
                root.setExpanded(true);
                Platform.runLater(() -> explorerView.setRoot(root));
                if (splitPane.getDividerPositions()[0] < 0.01) {
                    Platform.runLater(() -> {
                        KeyValue keyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), 0.25,
                                Interpolator.EASE_OUT);
                        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), keyValue));
                        timeline.play();
                    });
                }
                explorerView.getRoot().getChildren().forEach(item -> item.getChildren());
                return null;
            }
        }).start();

        startNewBGWatcher(file);
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

    public void menuSave(Event e) {
        NoteTab tab = (NoteTab) tabPane.getSelectionModel().getSelectedItem();
        if (tab.getFile() == null) {
            menuSaveAs(e);
            return;
        }
        if (tab.getFile().canWrite()) {
            saveTextToFile(tab.getFile(), tab.getNote());
            tab.setModified(false);
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(splitPane.getScene().getWindow());
            alert.setHeaderText("This File Is Read-Only");
            alert.setContentText(
                    "Read-only is a file attribute which only allows a user to view a file, restricting any writing to the file.");
            alert.showAndWait();
            menuSaveAs(e);
        }
    }

    public void menuSaveAs(Event e) {
        saveChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
        NoteTab tab = (NoteTab) tabPane.getSelectionModel().getSelectedItem();
        if (tab.getFile() != null) {
            saveChooser.setInitialDirectory(tab.getFile().getParentFile());
            saveChooser.setInitialFileName(tab.getFile().getName().replace("*", ""));
        } else {
            saveChooser.setInitialFileName(tab.getText().replace("*", "") + ".txt");
        }
        File file = saveChooser.showSaveDialog(null);
        if (file != null) {
            saveTextToFile(file, tab.getNote());
            tab.setModified(false);
            tab.setText(file.getName());
            tab.setFileWithoutCheck(file);
        } else {
            e.consume();
            tabPane.setTabDragPolicy(TabDragPolicy.FIXED);
            new Timeline(new KeyFrame(Duration.millis(1000), ae -> tabPane.setTabDragPolicy(TabDragPolicy.REORDER))).play();
        }
    }

    public void setExplorerView(TreeView<NameFile> explorerView) {
        this.explorerView = explorerView;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public void setSplitPane(SplitPane splitPane) {
        this.splitPane = splitPane;
    }

    public void selectItem(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && !(event.getTarget() instanceof Group)) {
            TreeItem<NameFile> item = explorerView.getSelectionModel().getSelectedItem();
            if (item == null) {
                return;
            }
            File file = item.getValue();
            newTabInTabPane(file);
        }
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
            tab.setModified(false);
        } catch (FileIsDirectoryException e2) {
            return;
        }
        for (Tab tab2 : tabPane.getTabs()) {
            NoteTab noteTab = (NoteTab) tab2;
            if (noteTab.getFile() != null && noteTab.getFile().equals(file)) {
                tabPane.getSelectionModel().select(tab2);
                if (!noteTab.wasModified()) {
                    updateNoteTab(noteTab);
                }
                return;
            }
        }
        tabSetOnCloseRequest(tab);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public NoteTab tabSetOnCloseRequest(NoteTab tab) {
        tab.setOnCloseRequest(event -> {
            if (tab.wasModified()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Padcode");
                alert.setHeaderText("Do you want to save changes to " + tab.getText().replace("*", "") + "?");
                alert.setGraphic(null);
                alert.initOwner(splitPane.getScene().getWindow());
                ButtonType yesButton = new ButtonType("Yes", ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);
                var result = alert.showAndWait();
                if (result.get() == yesButton) {
                    menuSave(event);
                } else if (result.get() == cancelButton) {
                    event.consume();
                    tabPane.setTabDragPolicy(TabDragPolicy.FIXED);
                    new Timeline(new KeyFrame(Duration.millis(1000), ae -> tabPane.setTabDragPolicy(TabDragPolicy.REORDER))).play();
                    // There is a bug when you consume event and you instantly drag a tab
                }
            }
        });
        return tab;
    }

    private void startNewBGWatcher(File file) {
        bgWatcher = new Thread(new Task<Void>() {
            protected Void call() throws Exception {
                Path logDir = file.toPath();
                Kind<?>[] events = { ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE };
                try {
                    logDir.register(watcher, events, ExtendedWatchEventModifier.FILE_TREE);
                    while (bgWatcher == Thread.currentThread()) {
                        WatchKey key = watcher.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            Path correctPath = FileSystems.getDefault().getPath(logDir.toString(),
                                    ((Path) event.context()).toString());
                            File target = correctPath.toFile();
                            if (target.isHidden()) {
                                continue;
                            }
                            Stack<File> dirPath = new Stack<>();
                            dirPath.add(target);
                            boolean hiddenParent = false;
                            for (File parent = target.getParentFile(); !parent.toPath().equals(logDir); parent = parent
                                    .getParentFile()) {
                                dirPath.add(parent);
                                if (parent.isHidden()) {
                                    hiddenParent = true;
                                    break;
                                }
                            }
                            if (hiddenParent) {
                                continue;
                            }

                            TreeItem<NameFile> searchParent = null;
                            TreeItem<NameFile> search = explorerView.getRoot();
                            while (!dirPath.empty()) {
                                File lookTo = dirPath.pop();
                                searchParent = search;
                                search = searchInTree(search, lookTo);
                            }

                            if (searchParent == null) {
                                continue;
                            }

                            if (ENTRY_CREATE.equals(kind)) {
                                if (search == null) {
                                    Image ico = FileTreeItem.jswingIconToImage(
                                            FileSystemView.getFileSystemView().getSystemIcon(target));
                                    TreeItem<NameFile> item = new TreeItem<NameFile>(new NameFile(target));
                                    item.setGraphic(new ImageView(ico));
                                    boolean flag = true;
                                    for (int i = 0; i < searchParent.getChildren().size(); i++) {
                                        TreeItem<NameFile> child = searchParent.getChildren().get(i);
                                        String itemName = item.getValue().getName().toLowerCase();
                                        String childName = child.getValue().getName().toLowerCase();
                                        if (itemName.compareTo(childName) < 0) {
                                            searchParent.getChildren().add(i, item);
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if (flag) {
                                        searchParent.getChildren().add(item);
                                    }
                                }
                            } else if (ENTRY_MODIFY.equals(kind)) {
                                if (search != null && search.getValue().isFile()) {
                                    for (Tab tab2 : tabPane.getTabs()) {
                                        NoteTab noteTab = (NoteTab) tab2;
                                        if (noteTab.getFile() != null && noteTab.getFile().equals(search.getValue())) {
                                            if (!noteTab.wasModified()) {
                                                Platform.runLater(() -> updateNoteTab(noteTab));
                                            }
                                        }
                                    }
                                }
                            } else if (ENTRY_DELETE.equals(kind)) {
                                if (search != null) {
                                    searchParent.getChildren().remove(search);
                                }
                            }
                        }
                        key.reset();
                    }
                } catch (IOException | ClosedWatchServiceException ex) {
                    System.out.println(ex);
                }
                return null;
            }
        });
        bgWatcher.setDaemon(true);
        bgWatcher.start();
    }

    private TreeItem<NameFile> searchInTree(TreeItem<NameFile> tree, File lookTo) {
        if (tree == null) {
            return null;
        }
        for (TreeItem<NameFile> treeItem : tree.getChildren()) {
            if (treeItem.getValue().equals(lookTo)) {
                return treeItem;
            }
        }
        return null;
    }

    void updateNoteTab(NoteTab noteTab) {
        try {
            noteTab.setFile(noteTab.getFile());
            noteTab.setModified(false);
        } catch (FileIsDirectoryException | FileIsNotTextException e) {

        }
    }
}
