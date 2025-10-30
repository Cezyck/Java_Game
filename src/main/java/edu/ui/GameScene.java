package edu.ui;

import edu.engine.Keys;
import edu.engine.SceneController;
import edu.game.Enemy;
import edu.game.Player;
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
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class GameScene {
    private static final double W = SceneController.WIDTH;
    private static final double H = SceneController.HEIGHT;

    private final Keys keys = new Keys();
    private boolean paused = false;
    private int score = 0;
    private int wave = 1;
    private final List<double[]> stars = new ArrayList<>();
    private static final int NUM_STARS = 70;
    private Player player = new Player(W / 2.0, H - 80);
    private final List<Enemy> enemies = new ArrayList<>();

    @SuppressWarnings("CallToPrintStackTrace")
    public Scene create() {
        Canvas canvas = new Canvas(W, H);
        GraphicsContext g = canvas.getGraphicsContext2D();

        Button resume = createGameButton("CONTINUE");
        Button backToMenu = createGameButton("MAIN MENU");

        VBox overlay = new VBox(20, resume, backToMenu);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(40));
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        overlay.setVisible(false);
        overlay.setMouseTransparent(true);

        StackPane root = new StackPane(canvas, overlay);
        Scene scene = new Scene(root, W, H, Color.BLACK);

        keys.attach(scene);

        // Обработчик ESCAPE
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                paused = !paused;
                overlay.setVisible(paused);
                overlay.setMouseTransparent(!paused);
            }
        });

        resume.setOnAction(e -> {
            paused = false;
            overlay.setVisible(false);
            overlay.setMouseTransparent(true);
        });

        backToMenu.setOnAction(e -> {
            try {
                // Предполагается, что MainMenuScene существует
                Class<?> mainMenuClass = Class.forName("edu.ui.MainMenuScene");
                Scene mainMenuScene = (Scene) mainMenuClass.getDeclaredMethod("create").invoke(mainMenuClass.newInstance());
                SceneController.set(mainMenuScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        for (int i = 0; i < NUM_STARS; i++) {
            double x = Math.random() * W;
            double y = Math.random() * H;
            double size = Math.random() * 2 + 0.5;
            stars.add(new double[]{x, y, size});
        }

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
                    player.update(dt, now, keys, enemies);

                    for (Enemy enemy : enemies) {
                        enemy.update(dt, W);
                    }

                    Enemy.moveAllDown(enemies);

                    checkCollisions();
                    checkGameOver();
                }

                render(g);
            }
        };
        loop.start();

        return scene;
    }

    private void render(GraphicsContext g) {
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, W, H);

        g.setFill(Color.WHITE);
        for (double[] star : stars) {
            double x = star[0];
            double y = star[1];
            double size = star[2];
            g.fillOval(x, y, size, size);
        }

        for (Enemy enemy : enemies) {
            enemy.renderEnemy(g);
        }

        player.render(g);

        g.setFill(Color.LIME);
        g.setFont(Font.font("Arial", 16));
        g.fillText("SCORE: " + String.format(String.valueOf(score)), 20, 30);
        g.fillText("LIVES: " + player.getLives(), W - 100, 30);
        g.fillText("WAVE: " + wave, W / 2 - 30, 30);

        g.setFill(Color.WHITE);
        g.setFont(Font.font("Arial", 12));
        g.fillText("Enemies: " + enemies.size(), 20, H - 20);
    }

    private void spawnEnemies() {
        Enemy.resetGlobalState();
        enemies.clear();

        int rows = 5;
        int cols = 8;
        double startX = 50;
        double startY = 80;
        double spacingX = 70;
        double spacingY = 60;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * spacingX;
                double y = startY + row * spacingY;
                enemies.add(new Enemy(x, y));
            }
        }
    }

    private void checkCollisions() {
        List<edu.game.Bullet> bulletsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (edu.game.Bullet bullet : player.getBullets()) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && enemy.collidesWith(bullet)) {
                    bulletsToRemove.add(bullet);
                    enemiesToRemove.add(enemy);
                    score += 100;
                    break;
                }
            }
        }

        player.getBullets().removeAll(bulletsToRemove);
        for (Enemy enemy : enemiesToRemove) {
            enemy.destroy();
        }

        enemies.removeIf(e -> !e.isAlive());

        if (enemies.isEmpty()) {
            wave++;
            score += 500;
            spawnEnemies();
        }
    }

    private void checkGameOver() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.getY() > H - 150) {
                gameOver();
                return;
            }
        }

        if (player.getLives() <= 0) {
            gameOver();
        }
    }

    private void gameOver() {
        player = new Player(W / 2.0, H - 60);
        wave = 1;
        score = 0;
        spawnEnemies();
    }

    private Button createGameButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(40);

        button.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: #00ff00; " +
                        "-fx-border-color: #00ff00; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
        );

        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: #003300; " +
                            "-fx-text-fill: #00ff00; " +
                            "-fx-border-color: #00ff00; " +
                            "-fx-border-width: 3px; " +
                            "-fx-font-family: 'Arial'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold;"
            );
        });

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