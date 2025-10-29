import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage){
        stage.setTitle("Космическая битва");
        stage.setResizable(false);

        SceneController.init(stage, 520, 900);
        Scene menu = new MainMenuScene().create();
        SceneController.set(menu);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}