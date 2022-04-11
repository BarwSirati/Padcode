import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ui.Padcode;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Padcode().getScene());
        primaryStage.setTitle("Padcode");
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/asset/img/icon.png")));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}