package edu.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy {
    private double x;
    private double y;
    private int W = 42;
    private int H = 26;

    //скорость по X
    private double vx = 45; //пикселей в секунду вправо
    private double vy = 0; // пикселей в секунду вниз

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void update (double dt, double worldW){
        x += vx * dt;
        y += vy * dt;

        if(x<20){
            x=20;
            vx = -vx;
        } else if (x + W > worldW - 20) {
            x = worldW - 20 - W;
            vx = -vx;
        }
        //Можно добавить медленное спускание
        // y += vy * dt
    }
    public void renderEnemy(GraphicsContext g){
        g.setFill(Color.web("#FF5C5C"));
        g.fillRoundRect(x, y, W, H, 6,6 );
        g.setFill(Color.web("#990000"));
        g.fillRect(x+10, y + 6 , 5, 5);//глаза врага
        g.fillRect(x+26, y + 6 , 5, 5);

    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public int getW() {
        return W;
    }
    public int getH() {
        return H;
    }
}
