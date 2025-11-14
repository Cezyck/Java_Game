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


import java.util.List;

public class HighScoreScene {
    private final ArcadeButton arcadeButton = new ArcadeButton();

    public Scene create() {
        // Стилизованный заголовок
        Label title = new Label("High Scores");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        title.setTextFill(Color.LIME);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, #00ff00, 10, 0, 0, 0);");

        // Заголовок таблицы рекордов - выровнен по центру сверху
        Label header = new Label("TOP 10 SCORES");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        header.setTextFill(Color.YELLOW);
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);

        // Контейнер для результатов
        VBox scoresBox = new VBox(10);
        scoresBox.setAlignment(Pos.CENTER);

        List<HighScoreManager.Entry> highScores = HighScoreManager.top();

        if (highScores.isEmpty()) {
            Label noScores = new Label("No high scores yet!");
            noScores.setFont(Font.font("Arial", 20));
            noScores.setTextFill(Color.LIME);
            scoresBox.getChildren().add(noScores);
        } else {
            // Отображение каждого результата
            for (int i = 0; i < highScores.size(); i++) {
                HighScoreManager.Entry entry = highScores.get(i);
                Label scoreLabel = new Label(String.format("%d. %s - %d", i + 1, entry.name, entry.score));
                scoreLabel.setFont(Font.font("Arial", 20));
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

        Button mainMenu = arcadeButton.createArcadeButton("Main Menu");
        mainMenu.setOnAction(e -> SceneController.set(new MainMenuScene().create()));

        VBox box = new VBox(20.0, title, header, scoresBox, mainMenu);
        box.setPadding(new Insets(40, 140, 40, 140));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: black;");


        VBox.setMargin(mainMenu, new Insets(60, 0, 0, 0));

        Scene scene = new Scene(box, SceneController.WIDTH, SceneController.HEIGHT);
        scene.setFill(Color.BLACK);

        return scene;
    }
}