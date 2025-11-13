package edu.ui;

import edu.engine.Keys;
import edu.engine.SceneController;
import edu.engine.HighScoreManager;
import edu.game.Bullet;
import edu.game.Enemy;
import edu.game.Player;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;


public class GameScene {
    private static final double W = SceneController.WIDTH;
    private static final double H = SceneController.HEIGHT;
    private final Keys keys = new Keys();
    private boolean paused = false;
    private  boolean isGameOver = false;
    private int score = 0;
    private int wave = 1;
    private final List<double[]> stars = new ArrayList<>();
    private static final int NUM_STARS = 100;
    private Player player = new Player(W / 2.0, H - 80);
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> enemyBullet = new ArrayList<>();
    private StackPane root; // Ссылка на корневой элемент
    private final ArcadeButton arcadeButton = new ArcadeButton();
    private boolean scoreSaved = false;

    public Scene create() {
        Canvas canvas = new Canvas(W, H);
        canvas.setMouseTransparent(true);
        GraphicsContext g = canvas.getGraphicsContext2D();

        Button resume = arcadeButton.createArcadeButton("CONTINUE");
        Button backToMenu = arcadeButton.createArcadeButton("MAIN MENU");

        VBox overlay = new VBox(20, resume, backToMenu);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(40));
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        overlay.setVisible(false);
        overlay.setMouseTransparent(!paused);

        StackPane root = new StackPane(canvas, overlay );
        this.root = root; // Сохраняем ссылку
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

        backToMenu.setOnAction(e -> SceneController.set(new MainMenuScene().create()));

        for (int i = 0; i < NUM_STARS; i++) {
            double x = Math.random() * W;
            double y = Math.random() * H;
            double size = Math.random() * 2 + 0.5;
            stars.add(new double[]{x, y, size});
        }

        spawnEnemies();

        AnimationTimer gameLoop = new AnimationTimer() {
            long prev = 0;

            @Override
            public void handle(long now) {
                if (prev == 0) {
                    prev = now;
                    return;
                }
                double dt = Math.min((double) (now - prev) / 1_000_000_000, 0.05);
                prev = now;

                if (!paused && !isGameOver && !enemies.isEmpty()) {
                    player.update(dt, now, keys, enemies, enemyBullet);

                    long  aliveEnemiesCount = enemies.stream()
                            .filter(Enemy::isAlive)
                            .count();
                    Enemy.enemySpeed((int) aliveEnemiesCount, wave);
                    for (Enemy enemy : enemies) {
                        enemy.update(dt, W, (int) aliveEnemiesCount, wave);
                    }

                    enemyBullet.removeIf(bullet -> !bullet.update(dt));
                    Enemy.updateEnemyShooting(now, enemies, wave);
                    Enemy.moveAllDown(enemies);
                    int[] results = Enemy.checkCollisionsEnemy(enemies, player.getBullets(), wave, score, () -> spawnEnemies());
                    score = results[0];
                    wave = results[1];
                    player.checkCollisionsPlayer(enemyBullet);
                    checkGameOver();
                }
                render(g);
            }
        };
        gameLoop.start();

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
        if (!isGameOver) {
            for (Enemy enemy : enemies) {
                enemy.renderEnemy(g);
                enemy.renderenemyBullet(g);
            }

            player.render(g);

            g.setFill(Color.LIME);
            g.setFont(Font.font("Arial", 16));
            g.fillText("SCORE: " + String.format(String.valueOf(score)), 20, 30);
            g.fillText("LIVES: " + player.getLives(), W - 100, 30);
            g.fillText("WAVE: " + wave, W / 2 - 30, 30);

            g.setFill(Color.LIME);
            g.setFont(Font.font("Arial", 16));
            g.fillText("Enemies: " + enemies.size(), 20, H - 20);
        }
    }

    private void spawnEnemies() {
        Enemy.resetGlobalState(wave);
        enemies.clear();

        int rows = 4;
        int cols = 5;
        double startX = 50;
        double startY = 80;
        double spacingX = 75;
        double spacingY = 60;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * spacingX;
                double y = startY + row * spacingY;
                long delay = 4500 + (long)(Math.random() * 3000);
                double shootChance = 0.01 + Math.random() * 0.4;
                enemies.add(new Enemy(x, y, delay, shootChance, enemyBullet ));
            }
        }
    }

    private void gameOver() {
        isGameOver = true;
        paused = true;

        // Проверяем, является ли результат рекордным
        boolean isHighScore = HighScoreManager.isHighScore(score);

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gameOverLabel.setTextFill(Color.LIME);
        gameOverLabel.setStyle("-fx-effect: dropshadow(three-pass-box, #00ff00, 10, 0, 0, 0);");

        Label scoreLabel = new Label("Your Score: " + score);
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreLabel.setTextFill(Color.LIME);

        VBox gameOverScene;

        if (isHighScore && !scoreSaved) {
            // Если результат рекордный - показываем поле для ввода имени
            Label highScoreLabel = new Label("NEW HIGH SCORE!");
            highScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            highScoreLabel.setTextFill(Color.YELLOW);

            Label nameLabel = new Label("Enter your name:");
            nameLabel.setFont(Font.font("Arial", 16));
            nameLabel.setTextFill(Color.LIME);

            TextField nameField = new TextField();
            nameField.setMaxWidth(200);
            nameField.setStyle("-fx-background-color: black; -fx-text-fill: lime; -fx-border-color: lime;");
            nameField.setPromptText("Player");

            Button saveButton = arcadeButton.createArcadeButton("Save Score");
            Button mainMenu = arcadeButton.createArcadeButton("Main Menu");
            Button retry = arcadeButton.createArcadeButton("Try Again");

            saveButton.setOnAction(e -> {
                String playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = "Player";
                }
                HighScoreManager.add(playerName, score);
                scoreSaved = true;
                saveButton.setVisible(false);
                mainMenu.setOnAction(event -> SceneController.set(new MainMenuScene().create()));
                retry.setOnAction(event -> resetGame());

            });

            gameOverScene = new VBox(15, gameOverLabel, highScoreLabel, scoreLabel, nameLabel, nameField, saveButton, retry, mainMenu);
        } else {
            // Обычный экран Game Over если не highScore
            Button mainMenu = arcadeButton.createArcadeButton("Main Menu");
            Button retry = arcadeButton.createArcadeButton("Try Again");

            mainMenu.setOnAction(e -> SceneController.set(new MainMenuScene().create()));
            retry.setOnAction(e -> resetGame());

            gameOverScene = new VBox(20, gameOverLabel, scoreLabel, retry, mainMenu);
        }

        gameOverScene.setAlignment(Pos.CENTER);
        gameOverScene.setPadding(new Insets(40));
        gameOverScene.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
        gameOverScene.setMouseTransparent(false);

        if (this.root != null) {
            this.root.getChildren().removeIf(node -> node instanceof VBox);
            this.root.getChildren().add(gameOverScene);
            gameOverScene.toFront();
        }
    }
    private void checkGameOver() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.getY() > H - 250) {
                gameOver();
                return;
            }
        }

        if (player.getLives() <= 0) {
            gameOver();
        }
    }
    private void resetGame(){
        if (this.root != null) {
            this.root.getChildren().removeIf(node -> node instanceof VBox);
        }
        isGameOver = false;
        score = 0;
        wave = 1;
        player = new Player(W / 2.0, H - 80);
        enemies.clear();
        enemyBullet.clear();
        spawnEnemies();
        paused = false;
        scoreSaved = false; // Сбрасываем флаг сохранения
    }
}