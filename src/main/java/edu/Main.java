package edu;

import edu.engine.SceneController;
import edu.engine.SceneManager;
import edu.ui.MainMenuScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application implements SceneManager {

    private Stage primaryStage; // Делаем primaryStage полем класса

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Сохраняем ссылку на primaryStage

        SceneController.initialize(primaryStage, this);

        // Начинаем с главного меню
        SceneController.set(new MainMenuScene().create());

        primaryStage.setTitle("Space Invaders");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void setScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}