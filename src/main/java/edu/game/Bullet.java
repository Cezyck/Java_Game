package edu.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {
    private double x;
    private double y;
    private double vy;
    private final int WIDTH = 4;
    private final int HEIGHT = 12;
    private final Color color;

    public Bullet(double x, double y, double vy) {
        this.x = x;
        this.y = y;
        this.vy = vy;
        this.color = Color.web("#00C2FF");
    }

    public Bullet(double x, double y, double vy, Color color) {
        this.x = x;
        this.y = y;
        this.vy = vy;
        this.color = color;
    }

    public boolean update(double dt) {
        y += vy * dt;
        return !isOffScreen(); // ИСПРАВЛЕНО: возвращаем true, пока пуля активна
    }

    public boolean isOffScreen() {
        return y < -HEIGHT; // ИСПРАВЛЕНО: проверяем только верхнюю границу
    }

    public void render(GraphicsContext g) {
        g.setFill(color);
        g.fillRoundRect(x - WIDTH/2, y - HEIGHT/2, WIDTH, HEIGHT, 4, 4);

        // Эффект свечения пули
        g.setFill(Color.web("#FFFFFF"));
        g.fillRoundRect(x - 1, y - HEIGHT/2 + 2, 2, HEIGHT - 4, 2, 2);
    }

    // Геттеры для проверки столкновений
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
}