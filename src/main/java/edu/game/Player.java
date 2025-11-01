package edu.game;

import edu.engine.Keys;
import edu.engine.SceneController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private static final Image SPRITE = new Image("/Models/player-ship.png");
    private double x, y;
    private final double WIDTH = 120;
    private final double HEIGHT = 100;
    private  int lives = 3;
    private final List<Bullet> bullets = new ArrayList<>();

    private long lastShotTime = 0;
    private static final long SHOOT_DELAY = 400_000_000; // 0.4 сек

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double dt, long now, Keys keys, List<Enemy> enemies) {
        double moveX = 0, moveY = 0;

        // 🔧 управление: WASD
        if (keys.isDown(KeyCode.A)) moveX -= 1;
        if (keys.isDown(KeyCode.D)) moveX += 1;
        if (keys.isDown(KeyCode.W)) moveY -= 1;
        if (keys.isDown(KeyCode.S)) moveY += 1;

        // ➡️ Движение
        double speed = 250;
        x += moveX * speed * dt;
        y += moveY * speed * dt;

        // 🔧 Границы экрана (горизонталь)
        x = Math.max(0, Math.min(x, SceneController.WIDTH - WIDTH));

        // 💡 ИСПРАВЛЕНИЕ: Новая логика ограничения Y (держим корабль под врагами)
        double topBoundary = getTopBoundary(enemies);

        // 🔧 Границы экрана (вертикаль)
        // Игрок не может подняться ВЫШЕ (y < topBoundary)
        // И не может опуститься НИЖЕ (y > H - HEIGHT)
        y = Math.max(topBoundary, Math.min(y, SceneController.HEIGHT - HEIGHT));

        // 🔫 Обновление пуль
        bullets.removeIf(b -> !b.update(dt));

        // 🔫 Стрельба на SPACE
        if (keys.isDown(KeyCode.SPACE)) {
            shoot(now);
        }
    }

    private static double getTopBoundary(List<Enemy> enemies) {
        double maxEnemyBottomY = 0; // Ищем "дно" самого нижнего живого врага
        for (Enemy e : enemies) {
            if (e.isAlive()) { // Если враг жив, учитываем его
                maxEnemyBottomY = Math.max(maxEnemyBottomY, e.getY() + e.getHeight());
            }
        }

        // Устанавливаем "барьер" (верхняя граница для игрока)
        // Игрок должен быть минимум на 20px ниже нижнего врага (если враги есть)
        // Если врагов нет (maxEnemyBottomY = 0), то topBoundary = 0 (верх экрана).
        return (maxEnemyBottomY > 0) ? (maxEnemyBottomY + 20) : 0;
    }

    private void shoot(long now) {
        if (now - lastShotTime > SHOOT_DELAY) {
            // Передаем отрицательную скорость (-600) для полета вверх
            bullets.add(new Bullet(x + WIDTH / 2 - 2, y - 10, -600));
            lastShotTime = now;
        }
    }

    public  void takeDamage(){
        if(lives > 0){
            lives--;
        }
    }


    public void render(GraphicsContext g) {
        // Добавление изображения
        g.drawImage(SPRITE, x, y, WIDTH, HEIGHT);

        // Отрисовка пуль остается
        for (Bullet b : bullets) b.render(g);
    }

    public int getLives() {
        return lives;
    }

    public double getX() {
        return x;
    }

    public double getWidth() {
        return WIDTH;
    }

    public double getHeight() {
        return HEIGHT;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public double getY() {
        return y;
    }
    //проверка на попадание в игрока
    public boolean collidesWith(Bullet Bullet) {
        return Bullet.getX() >= x &&
                Bullet.getX() <= x + WIDTH &&
                Bullet.getY() >= y &&
                Bullet.getY() <= y + HEIGHT;
    }

}