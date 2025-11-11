package edu.ui;

import edu.engine.Keys;
import edu.engine.SceneController;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class GameScene {
    private static final double W = SceneController.WIDTH;
    private static final double H = SceneController.HEIGHT;
    private long lastEnemyShotTime = 0;
    private final Keys keys = new Keys();
    private boolean paused = false;
    private  boolean isGameOver = false;
    private int score = 0;
    private int wave = 1;
    private final List<double[]> stars = new ArrayList<>();
    private static final int NUM_STARS = 70;
    private Player player = new Player(W / 2.0, H - 80);
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> enemyBullet = new ArrayList<>();
    private StackPane root; // Ссылка на корневой элемент

    public Scene create() {
        Canvas canvas = new Canvas(W, H);
        canvas.setMouseTransparent(true);
        GraphicsContext g = canvas.getGraphicsContext2D();

        Button resume = createGameButton("CONTINUE");
        Button backToMenu = createGameButton("MAIN MENU");

        VBox overlay = new VBox(20, resume, backToMenu);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(40));
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        overlay.setVisible(false);
        overlay.setMouseTransparent(!paused);

        StackPane root = new StackPane(canvas, overlay);
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

                if (!paused && !isGameOver) {
                    player.update(dt, now, keys, enemies);


                    long aliveEnemiesCount = enemies.stream()
                            .filter(Enemy::isAlive)
                            .count();
                    for (Enemy enemy : enemies) {
                        enemy.update(dt, W, (int) aliveEnemiesCount, wave);
                    }

                    enemyBullet.removeIf(bullet -> !bullet.update(dt));
                    updateEnemyShooting(now);
                    Enemy.moveAllDown(enemies);
                    checkCollisionsEnemy();
                    checkCollisionsPlayer();
                    checkGameOver();
                }
                render(g);
            }
        };
        loop.start();

        return scene;
    }

    private void updateEnemyShooting(long now) {
        // 3 секунды между выстрелами врагов
        long ENEMY_SHOOT_INTERVAL = 3_000_000_000L;
        if (now - lastEnemyShotTime >= ENEMY_SHOOT_INTERVAL) {
            List<Enemy> availableEnemies = new ArrayList<>();
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    availableEnemies.add(enemy);
                }
            }

            // Считаем количество живых врагов
            int aliveEnemiesCount = availableEnemies.size();

            Collections.shuffle(availableEnemies);
            int MAX_SHOOTING_ENEMIES = 5;
            int enemiesToShoot = Math.min(MAX_SHOOTING_ENEMIES, availableEnemies.size());

            for (int i = 0; i < enemiesToShoot; i++) {
                availableEnemies.get(i).shoot(aliveEnemiesCount);
            }

            lastEnemyShotTime = now;
        }
    }


    private void render(GraphicsContext g) {
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, W, H);

            // Отрисовка игровых объектов только если игра не завершена
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
        double spacingX = 70;
        double spacingY = 60;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * spacingX;
                double y = startY + row * spacingY;
                long delay = 4500 + (long)(Math.random() * 3000);
                double shootChance = 0.01 + Math.random() * 0.3;
                enemies.add(new Enemy(x, y, delay, shootChance, enemyBullet ));
            }
        }
    }

    private void checkCollisionsEnemy() {
        Iterator<Bullet> bulletIterator = player.getBullets().iterator();

        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            boolean bulletHit = false;

            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext() && !bulletHit) {
                Enemy enemy = enemyIterator.next();
                if (enemy.isAlive() && enemy.collidesWith(bullet)) {
                    bulletIterator.remove(); // Удаляем пулю
                    enemy.destroy(); // Уничтожаем врага
                    score += 100;
                    bulletHit = true; // Пуля попала, выходим из цикла
                }
            }
        }

        // Удаляем мертвых врагов
        enemies.removeIf(e -> !e.isAlive());

        if (enemies.isEmpty()) {
            wave++;
            score += 500;
            spawnEnemies();
        }
    }
    private void checkCollisionsPlayer(){
        Iterator<Bullet> iterator = enemyBullet.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            if (player.getLives() > 0 && player.collidesWith(bullet)) {
                iterator.remove(); // Безопасное удаление из списка
                player.takeDamage();
            }
        }
    }

    private void gameOver() {
        isGameOver = true;
        paused = true;

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gameOverLabel.setTextFill(Color.LIME);
        gameOverLabel.setStyle("-fx-effect: dropshadow(three-pass-box, #00ff00, 10, 0, 0, 0);");

        Button mainMenu = createGameButton("Main Menu");
        Button retry = createGameButton("Try Again");

        VBox gameOverScene = new VBox(20, gameOverLabel, retry, mainMenu);
        gameOverScene.setAlignment(Pos.CENTER);
        gameOverScene.setPadding(new Insets(40));
        gameOverScene.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        gameOverScene.setMouseTransparent(false); // Убедитесь, что панель не прозрачна для мыши

        // Удаляем существующий оверлей game over, если он есть
        if (this.root != null) {
            // Удаляем все существующие оверлеи game over
            this.root.getChildren().removeIf(node -> node instanceof VBox && node != gameOverScene);

            // Добавляем новый оверлей поверх всего
            this.root.getChildren().add(gameOverScene);
            gameOverScene.toFront(); // Важно: помещаем поверх всего
        }

        mainMenu.setOnAction(e -> SceneController.set(new MainMenuScene().create()));

        retry.setOnAction(e -> {
            // Перезапуск игры
            resetGame();
            // Удаляем gameOverScene из root
            if (this.root != null) {
                this.root.getChildren().remove(gameOverScene);
            }
            // Сбрасываем состояние паузы
            paused = false;
            isGameOver = false;
        });
    }

    private void checkGameOver() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.getY() > H - 200) {
                gameOver();
                return;
            }
        }

        if (player.getLives() <= 0) {
            gameOver();
        }
    }


    private void resetGame(){
        isGameOver = false;
        score = 0;
        wave = 1;
        player = new Player(W / 2.0, H - 80);
        enemies.clear();
        enemyBullet.clear();
        spawnEnemies();
        paused = false;
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