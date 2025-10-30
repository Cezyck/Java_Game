package edu.engine;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {
    public static final double WIDTH = 800;
    public static final double HEIGHT = 600;

    private static Stage primaryStage;
    private static SceneManager sceneManager;

    public static void initialize(Stage stage, SceneManager manager) {
        primaryStage = stage;
        sceneManager = manager;
    }

    public static void set(Scene scene) {
        if (sceneManager != null) {
            sceneManager.setScene(scene);
        } else if (primaryStage != null) {
            primaryStage.setScene(scene);
        }
    }
}