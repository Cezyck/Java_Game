package edu.ui;

import edu.engine.SceneController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainMenuScene {
    public Scene create(){
        // Стилизованный заголовок
        Label title = new Label("SPACE INVADERS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.LIME);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, #00ff00, 10, 0, 0, 0);");

        // Кнопки в стиле ретро-аркады
        Button start = createArcadeButton("START GAME");
        Button highScore = createArcadeButton("HIGH SCORE");
        Button author = createArcadeButton("AUTHORS");
        Button exit = createArcadeButton("EXIT");

        // Обработка нажатия на START - переход на игровую сцену
        start.setOnAction(e -> {
            // Используем reflection или создаем экземпляр через класс
            try {
                Class<?> gameSceneClass = Class.forName("edu.ui.GameScene");
                Scene gameScene = (Scene) gameSceneClass.getDeclaredMethod("create").invoke(gameSceneClass.newInstance());
                SceneController.set(gameScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Обработка выхода
        exit.setOnAction(e -> System.exit(0));

        // Остальные кнопки (заглушки)
        highScore.setOnAction(e -> {
            // Будет реализовано позже
        });

        author.setOnAction(e -> {
            SceneController.set(new AuthorScene().create());
        });

        VBox box = new VBox(20.0, title, start, highScore, author, exit);
        box.setPadding(new Insets(40));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(box, SceneController.WIDTH, SceneController.HEIGHT);
        scene.setFill(Color.BLACK);

        return scene;
    }

    private Button createArcadeButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(50);

        // Стиль в духе аркадных автоматов
        button.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: #00ff00; " +
                        "-fx-border-color: #00ff00; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-effect: dropshadow(three-pass-box, #00ff00, 5, 0, 0, 0);"
        );

        // Эффекты при наведении
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: #003300; " +
                            "-fx-text-fill: #00ff00; " +
                            "-fx-border-color: #00ff00; " +
                            "-fx-border-width: 3px; " +
                            "-fx-font-family: 'Arial'; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-effect: dropshadow(three-pass-box, #00ff00, 8, 0, 0, 0);"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: black; " +
                            "-fx-text-fill: #00ff00; " +
                            "-fx-border-color: #00ff00; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-family: 'Arial'; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-effect: dropshadow(three-pass-box, #00ff00, 5, 0, 0, 0);"
            );
        });

        return button;
    }
}