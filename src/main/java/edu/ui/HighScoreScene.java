package edu.ui;

import edu.engine.SceneController;
import edu.engine.HighScoreManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class HighScoreScene {
    public Scene create(){
        // Стилизованный заголовок
        Label title = new Label("High Score");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.LIME);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, #00ff00, 10, 0, 0, 0);");

        // Загрузка и отображение таблицы рекордов
        VBox scoresBox = new VBox(10);
        scoresBox.setAlignment(Pos.CENTER);

        List<HighScoreManager.Entry> highScores = HighScoreManager.top();

        if (highScores.isEmpty()) {
            Label noScores = new Label("No high scores yet!");
            noScores.setFont(Font.font("Arial", 18));
            noScores.setTextFill(Color.LIME);
            scoresBox.getChildren().add(noScores);
        } else {
            // Заголовок таблицы
            Label header = new Label("TOP 10 SCORES");
            header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            header.setTextFill(Color.YELLOW);
            scoresBox.getChildren().add(header);

            // Отображение каждого результата
            for (int i = 0; i < highScores.size(); i++) {
                HighScoreManager.Entry entry = highScores.get(i);
                Label scoreLabel = new Label(String.format("%d. %s - %d", i + 1, entry.name, entry.score));
                scoreLabel.setFont(Font.font("Arial", 16));
                scoreLabel.setTextFill(Color.LIME);

                // Выделение первых трех мест
                if (i == 0) {
                    scoreLabel.setTextFill(Color.GOLD);
                    scoreLabel.setStyle("-fx-font-weight: bold;");
                } else if (i == 1) {
                    scoreLabel.setTextFill(Color.SILVER);
                    scoreLabel.setStyle("-fx-font-weight: bold;");
                } else if (i == 2) {
                    scoreLabel.setTextFill(Color.ORANGE);
                    scoreLabel.setStyle("-fx-font-weight: bold;");
                }

                scoresBox.getChildren().add(scoreLabel);
            }
        }

        // Кнопки в стиле ретро-аркады
        Button mainMenu = createArcadeButton("Main Menu");
        mainMenu.setOnAction(e -> SceneController.set(new MainMenuScene().create()));

        VBox box = new VBox(20.0, title, scoresBox, mainMenu);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(box, SceneController.WIDTH, SceneController.HEIGHT);
        scene.setFill(Color.BLACK);

        return scene;
    }

    //стилизация копки в стиле ретро аркады
    private Button createArcadeButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(200);
        button.setPrefHeight(50);

        button.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: #00ff00; " +
                        "-fx-border-color: #00ff00; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #003300; " +
                        "-fx-text-fill: #00ff00; " +
                        "-fx-border-color: #00ff00; " +
                        "-fx-border-width: 3px; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: #00ff00; " +
                        "-fx-border-color: #00ff00; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
        ));
        return button;
    }
}