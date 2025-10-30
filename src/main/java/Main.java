import edu.engine.SceneController;
import edu.ui.MainMenuScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage){
        stage.setTitle("Space Evaders");
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