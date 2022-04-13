import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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
            boolean[] check = new boolean[tabsCount];
            for (int i = 0; i < tabsCount; i++) {
                Tab t = tabs.get(0);
                Event te = new Event(Tab.CLOSED_EVENT);
                t.getOnCloseRequest().handle(te);
                check[i] = te.isConsumed();
                if (!te.isConsumed()) {
                    Platform.runLater(() -> tabs.remove(0));
                }
            }
            for (int i = 0; i < check.length; i++) {
                if (check[i]) {
                    winEvent.consume();
                    break;
                }
            }
        });
        primaryStage.show();
    }
}