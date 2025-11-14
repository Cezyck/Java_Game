package edu.ui;

import javafx.scene.control.Button;

public class ArcadeButton {
    public Button createArcadeButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(50);

        // Основной стиль с внутренней тенью
        button.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: #00ff00; " +
                        "-fx-border-color: #00ff00; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 20px; " +
                        "-fx-background-radius: 20px; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-effect: dropshadow(gaussian, #00ff00, 10, 0.5, 0, 0);"
        );

        // Эффекты при наведении
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: #003300; " +
                            "-fx-text-fill: #00ff00; " +
                            "-fx-border-color: #00ff00; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 20px; " +
                            "-fx-background-radius: 20px; " +
                            "-fx-font-family: 'Arial'; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-effect: dropshadow(gaussian, #00ff00, 15, 0.7, 0, 0);"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: black; " +
                            "-fx-text-fill: #00ff00; " +
                            "-fx-border-color: #00ff00; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 20px; " +
                            "-fx-background-radius: 20px; " +
                            "-fx-font-family: 'Arial'; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-effect: dropshadow(gaussian, #00ff00, 10, 0.5, 0, 0);"
            );
        });

        return button;
    }
}