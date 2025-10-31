package edu.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


import java.util.List;
import java.util.Random;

public class Enemy{
    private static final Image SPRITE = new Image("/Models/alien.png");
    private double x;
    private double y;
    private final int WIDTH = 80;
    private final int HEIGHT = 50;
    private boolean alive = true;
    private final Random random = new Random();

    // Глобальные параметры
    private static double globalVx = 45;
    private static boolean shouldMoveDown = false;
    private static boolean boundaryHitThisFrame = false;
    private final long SHOOT_DELAY; // 0.7 сек
    private final List<Bullet> bullets;
    private final double shootChance;

    // Ограничение спуска (макс. Глубина)
    private static final double MAX_DESCENT_Y = 600;

    public Enemy(double x, double y, long SHOOT_DELAY, double shootChance, List<Bullet> enemyBullet ) {
        this.x = x;
        this.y = y;
        this.SHOOT_DELAY = SHOOT_DELAY;
        this.shootChance = shootChance;
        this.bullets = enemyBullet;
    }

    public void update(double dt, double worldW) {
        if (!alive) return;

        // Обычное горизонтальное движение
        x += globalVx * dt;

        boolean hitBoundary = false;
        if (x < 20) {
            x = 20;
            hitBoundary = true;
        } else if (x + WIDTH > worldW - 20) {
            x = worldW - 20 - WIDTH;
            hitBoundary = true;
        }

        if (hitBoundary && !boundaryHitThisFrame) {
            boundaryHitThisFrame = true;
            shouldMoveDown = true;
        }

    }



    public void shoot(int aliveEnemiesCount) {  // Добавляем параметр
        Color color = Color.RED;
        double bulletX = x + (double) WIDTH / 2;
        double bulletY = y + HEIGHT;

        // Базовая скорость + бонус когда врагов мало
        double baseSpeed = 300;

        // ИЗМЕНЕНИЕ: Формула для увеличения скорости, когда врагов МАЛО.
        // Предположим, что максимальное количество врагов при спав не - 20 (4 ряда * 5 колонок в GameScene.java).
        // Максимальное количество - это то, от чего мы будем отталкиваться.
        // Если врагов 20, (20 - 20) * 1.5 = 0 (бонус 0).
        // Если врагов 1, (20 - 1) * 1.5 = 28.5 (бонус 28.5).
        double MAX_ENEMIES_FOR_BONUS = 20;

        // Формула для увеличения скорости, когда врагов мало.
        double speedBonus = Math.max(0, (MAX_ENEMIES_FOR_BONUS - aliveEnemiesCount) * 1.5);

        // Ограничиваем максимальную скорость
        double finalSpeed = Math.min(baseSpeed + speedBonus, 400); // Увеличил лимит до 65

        bullets.add(new Bullet(bulletX, bulletY, finalSpeed, color));
    }

// ... (другие методы)

    public  void renderBullets(GraphicsContext gc) {
        for (Bullet bullet : bullets){
            bullet.render(gc);
        }
    }


    // Централизованный спуск всех врагов (Необходим для GameScene)
    public static void moveAllDown(List<Enemy> enemies) {
        if (!shouldMoveDown) return;

        double descentAmount = 15;
        for (Enemy e : enemies) {
            if (e.alive && e.y < MAX_DESCENT_Y) {
                e.y += descentAmount;
            }
        }

        globalVx = -globalVx;
        shouldMoveDown = false;
        boundaryHitThisFrame = false;
    }

    public void renderEnemy(GraphicsContext g) {
        if (!alive) return;

        // вывод изображения противника
        g.drawImage(SPRITE, x, y, WIDTH, HEIGHT);
    }

    public static void resetGlobalState() {
        globalVx = 45;
        shouldMoveDown = false;
        boundaryHitThisFrame = false;
    }

    public void destroy() {
        this.alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public boolean collidesWith(Bullet bullet) {
        if (!alive) return false;

        return bullet.getX() >= x &&
                bullet.getX() <= x + WIDTH &&
                bullet.getY() >= y &&
                bullet.getY() <= y + HEIGHT;
    }
}