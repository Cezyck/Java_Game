package edu.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

public class Enemy {
    private static final Image SPRITE = new Image("/Models/alien.png");
    private double x;
    private double y;
    private final int WIDTH = 80;
    private final int HEIGHT = 50;
    private boolean alive = true;

    // Глобальные параметры
    private static double globalVx = 45;
    private static boolean shouldMoveDown = false;
    private static boolean boundaryHitThisFrame = false;

    // Ограничение спуска (макс. Глубина)
    private static final double MAX_DESCENT_Y = 600;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
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

    // Централизованный спуск всех врагов (Необходим для GameScene)
    public static void moveAllDown(List<Enemy> enemies) {
        if (!shouldMoveDown) return;

        double descentAmount = 25;
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