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
    private final ArcadeButton arcadeButton = new ArcadeButton();
    public Scene create(){
        // Стилизованный заголовок
        Label title = new Label("SPACE INVADERS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.LIME);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, #00ff00, 10, 0, 0, 0);");

        // Кнопки в стиле ретро-аркады
        Button start = arcadeButton.createArcadeButton("START GAME");
        Button highScore = arcadeButton.createArcadeButton("HIGH SCORE");
        Button author = arcadeButton.createArcadeButton("AUTHORS");
        Button exit = arcadeButton.createArcadeButton("EXIT");

        // Обработка нажатия на START - переход на игровую сцену
        start.setOnAction(e -> {
            SceneController.set(new GameScene().create());
        });

        // Обработка выхода
        exit.setOnAction(e -> System.exit(0));

        //High Score сцены
        highScore.setOnAction(e -> {
            SceneController.set(new HighScoreScene().create());
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
}