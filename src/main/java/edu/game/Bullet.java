package edu.game;

import edu.engine.SceneController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {
    private double x;
    private double y;
    private double vy;
    private final int WIDTH = 4;
    private final int HEIGHT = 15;
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
        return !isOffScreen();
    }

    public boolean isOffScreen() {
        return y < -HEIGHT || y > SceneController.HEIGHT;
    }

    public void render(GraphicsContext g) {
        g.setFill(color);
        g.fillRoundRect(x - (double) WIDTH /2, y - (double) HEIGHT /2, WIDTH, HEIGHT, 4, 4);

        // Эффект свечения пули
        g.setFill(Color.web("#FFFFFF"));
        g.fillRoundRect(x - 1, y - (double) HEIGHT /2 + 2, 2, HEIGHT - 4, 2, 2);
    }

    // Геттеры для проверки столкновений
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}