import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ui.NoteTab;
import ui.Padcode;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Padcode padcode = new Padcode();
        primaryStage.setScene(padcode.getScene());
        primaryStage.setTitle("Padcode");
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/asset/img/icon.png")));
        primaryStage.setOnCloseRequest(winEvent -> {
            var tabs = padcode.controller.getTabPane().getTabs();
            int tabsCount = tabs.size();
            for (int i = 0; i < tabsCount; i++) {
                NoteTab t = (NoteTab) tabs.get(0);
                Event te = new Event(Tab.TAB_CLOSE_REQUEST_EVENT);
                t.getOnCloseRequest().handle(te);
                if (!te.isConsumed()) {
                    tabs.remove(0);
                } else {
                    winEvent.consume();
                    return;
                }
            }
            if (tabs.size() > 0) {
                winEvent.consume();
            }
        });
        padcode.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F5) {
                TabPane tp = padcode.controller.getTabPane();
                Tab selecting = tp.getSelectionModel().getSelectedItem();
                ObservableList<Tab> tabs = FXCollections.observableArrayList();
                tabs.addAll(tp.getTabs());
                tp.getTabs().clear();
                tp.getTabs().addAll(tabs);
                tp.getSelectionModel().select(selecting);
            }
        });
        primaryStage.show();
    }
}