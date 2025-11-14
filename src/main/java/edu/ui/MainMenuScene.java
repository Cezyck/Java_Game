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

        Label title = new Label("SPACE INVADERS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.LIME);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, #00ff00, 10, 0, 0, 0);");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);


        Button start = arcadeButton.createArcadeButton("START GAME");
        Button highScore = arcadeButton.createArcadeButton("HIGH SCORE");
        Button author = arcadeButton.createArcadeButton("AUTHORS");
        Button exit = arcadeButton.createArcadeButton("EXIT");


        start.setOnAction(e -> {
            SceneController.set(new GameScene().create());
        });


        exit.setOnAction(e -> System.exit(0));


        highScore.setOnAction(e -> {
            SceneController.set(new HighScoreScene().create());
        });

        author.setOnAction(e -> {
            SceneController.set(new AuthorScene().create());
        });


        VBox buttonsBox = new VBox(20.0, start, highScore, author, exit);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setMaxWidth(300);

        VBox box = new VBox(40.0, title, buttonsBox);
        box.setPadding(new Insets(70, 70, 70, 70));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: black;");


        VBox.setMargin(title, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(box, SceneController.WIDTH, SceneController.HEIGHT);
        scene.setFill(Color.BLACK);

        return scene;
    }
}