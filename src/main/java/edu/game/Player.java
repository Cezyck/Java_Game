package edu.game;

import edu.engine.Keys;
import edu.engine.SceneController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {
    private static final Image SPRITE = new Image("/Models/player-ship.png");
    private double x, y;
    private final double WIDTH = 120;
    private final double HEIGHT = 100;
    private int lives = 5;
    private final List<Bullet> bullets = new ArrayList<>();
    private long lastShotTime = 0;
    private static final long SHOOT_DELAY = 400_000_000; // 0.4 сек

    // Новые поля для неуязвимости и мерцания
    private boolean isInvulnerable = false;
    private long invulnerabilityStartTime = 0;
    private static final long INVULNERABILITY_DURATION = 600_000_000; // 0.6 секунды в наносекундах
    private boolean isVisible = true; // для мерцания
    private long lastBlinkTime = 0;
    private static final long BLINK_INTERVAL = 100_000_000; // 100ms между мерцаниями

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double dt, long now, Keys keys, List<Enemy> enemies, List<Bullet> enemyBullet) {
        // Обновление состояния неуязвимости и мерцания
        updateInvulnerability(now);

        double moveX = 0, moveY = 0;

        // 🔧 управление: WASD
        if (keys.isDown(KeyCode.A)) moveX -= 1;
        if (keys.isDown(KeyCode.D)) moveX += 1;
        if (keys.isDown(KeyCode.W)) moveY -= 1;
        if (keys.isDown(KeyCode.S)) moveY += 1;

        // ➡️ Движение
        double speed = 275;
        x += moveX * speed * dt;
        y += moveY * speed * dt;

        // 🔧 Границы экрана (горизонталь)
        x = Math.max(0, Math.min(x, SceneController.WIDTH - WIDTH));

        double topBoundary = getTopBoundary(enemies);

        // 🔧 Границы экрана (вертикаль)
        y = Math.max(topBoundary, Math.min(y, SceneController.HEIGHT - HEIGHT));

        // 🔫 Обновление пуль
        bullets.removeIf(b -> !b.update(dt));

        // 🔫 Стрельба на SPACE
        if (keys.isDown(KeyCode.SPACE)) {
            shoot(now);
        }

    }

    private void updateInvulnerability(long now) {
        if (isInvulnerable) {
            // Проверяем, закончился ли период неуязвимости
            if (now - invulnerabilityStartTime >= INVULNERABILITY_DURATION) {
                isInvulnerable = false;
                isVisible = true;
            } else {
                // Мерцание: переключаем видимость каждые BLINK_INTERVAL
                if (now - lastBlinkTime >= BLINK_INTERVAL) {
                    isVisible = !isVisible;
                    lastBlinkTime = now;
                }
            }
        }
    }

    public void checkCollisionsPlayer(List<Bullet> enemyBullet) {
        // Если игрок неуязвим - пропускаем проверку столкновений
        if (isInvulnerable) {
            return;
        }

        Iterator<Bullet> iterator = enemyBullet.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            if (lives > 0 && collidesWith(bullet)) {
                iterator.remove();
                takeDamage();
            }
        }
    }

    private static double getTopBoundary(List<Enemy> enemies) {
        double maxEnemyBottomY = 0; // Ищем самого нижнего живого врага
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                maxEnemyBottomY = Math.max(maxEnemyBottomY, enemy.getY() + enemy.getHeight());
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
            bullets.add(new Bullet(x + WIDTH / 2, y - 5, -600));
            lastShotTime = now;
        }
    }

    public void takeDamage() {
        if (lives > 0 && !isInvulnerable) {
            lives--;

            // Активируем неуязвимость после получения урона
            if (lives > 0) { // Только если игрок еще жив
                activateInvulnerability();
            }
        }
    }

    private void activateInvulnerability() {
        isInvulnerable = true;
        invulnerabilityStartTime = System.nanoTime();
        isVisible = true; // Начинаем с видимого состояния
        lastBlinkTime = System.nanoTime();
    }

    public void render(GraphicsContext g) {
        // Отрисовываем игрока только если он видим (для эффекта мерцания)
        if (isVisible) {
            g.drawImage(SPRITE, x, y, WIDTH, HEIGHT);
        }

        // Отрисовка пуль
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

    // Проверка на попадание в игрока
    public boolean collidesWith(Bullet bullet) {
        return bullet.getX() >= x &&
                bullet.getX() <= x + WIDTH &&
                bullet.getY() >= y &&
                bullet.getY() <= y + HEIGHT;
    }
}