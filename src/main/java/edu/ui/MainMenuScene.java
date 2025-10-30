package edu.ui;

import edu.engine.SceneController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


public class MainMenuScene {
    public Scene create(){
        Label title = new Label("Space Evaders");
        Button start = new Button("Start");
        Button High_Score = new Button("Highest Score");
        Button author = new Button("Authors");
        Button exit = new Button("Exit");

        start.setMaxWidth(Double.MAX_VALUE);
        High_Score.setMaxWidth(Double.MAX_VALUE);
        author.setMaxWidth(Double.MAX_VALUE);
        exit.setMaxWidth(Double.MAX_VALUE);


        VBox box = new VBox(16.0, title,start,  High_Score, author, exit);
        box.setPadding(new Insets(24));
        box.setAlignment(Pos.TOP_CENTER);


        return new Scene(box, SceneController.WIDTH, SceneController.HEIGHT);
    }
}
