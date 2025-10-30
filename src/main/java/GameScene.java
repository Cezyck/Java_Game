import edu.engine.Keys;
import edu.engine.SceneController;
import edu.game.Enemy;
import edu.game.Player;
import edu.ui.MainMenuScene;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class GameScene {
    private static final  double W = SceneController.WIDTH;
    private static final  double H = SceneController.HEIGHT;

    private  final Keys keys = new Keys();
    private boolean paused = false;

    private Player player = new Player(W/2.0, H - 140 );

    //Отрисовка врагов
    private final List<Enemy> enemies = new ArrayList<>();
    public Scene create(){
        Canvas canvas = new Canvas(W,H);
        GraphicsContext g = canvas.getGraphicsContext2D();

        //оверлей паузы
        Button resume =  new Button("Continue");
        Button backToMenu = new Button("Main menu");

        VBox overlay = new VBox(12,resume, backToMenu);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(10));
        overlay.setVisible(false);
        overlay.setMouseTransparent(true);

        StackPane root = new StackPane(canvas, overlay);
        Scene scene = new Scene(root, W,H, Color.WHITE);

        keys.attach(scene);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e ->{
            if(e.getCode() == KeyCode.ESCAPE){
                paused = !paused;
                overlay.setVisible(true);
                overlay.setMouseTransparent(!paused);
            }


        });

        resume.setOnAction(e -> {
            paused = false;
            overlay.setVisible(false);
            overlay.setMouseTransparent(!paused);
        });

        backToMenu.setOnAction(e -> {
            SceneController.set(new MainMenuScene().create());
        });

        //отрисовка врагов в начале сцены
        spawnEnemies();

        AnimationTimer loop = new AnimationTimer() {
            long prev = 0;

            @Override
            public void handle(long now) {
                if (prev == 0) {
                    prev = now;
                    return;
                }
                double dt = Math.min((double) (now - prev) / 1_000_000_000, 0.05);
                prev = now;
                if (!paused) {
                    player.update(dt, now, keys);
                    for (Enemy enemy : enemies) {
                        enemy.update(dt, W);
                    }
                }
                render(g);
            }
        };
        loop.start();
        return scene;


    }

    private void render(GraphicsContext g){
        g.setFill(Color.WHITE);
        g.fillRect(0,0,W,H);

        //HUD -
        for (Enemy enemy : enemies){
            enemy.renderEnemy(g);
        }
        player.render(g);

    }
    private void spawnEnemies(){
        enemies.clear();
        int row = 3;
        int cols = 4;
        double startX = 80;
        double startY = 110;
        enemies.add(new Enemy(startX, startY));
    }
}
