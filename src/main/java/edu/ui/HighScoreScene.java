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
import javafx.scene.text.TextAlignment;

public class HighScoreScene {
    public Scene create(){
        // Стилизованный заголовок
        Label title = new Label("High Score");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.LIME);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, #00ff00, 10, 0, 0, 0);");


        // Кнопки в стиле ретро-аркады
        Button mainMenu = createArcadeButton();
        mainMenu.setOnAction(e -> SceneController.set(new MainMenuScene().create()));

        VBox box = new VBox(20.0, title, mainMenu);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(box, SceneController.WIDTH, SceneController.HEIGHT);
        scene.setFill(Color.BLACK);

        return scene;
    }

    //стилизация копки в стиле ретро аркады
    private Button createArcadeButton() {
        Button button = new Button("Main Menu");
        button.setMaxWidth(Double.MAX_VALUE);
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
