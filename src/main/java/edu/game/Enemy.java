package edu.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.List;


public class Enemy{
    private static final Image SPRITE = new Image("/Models/alien.png");
    private double x;
    private double y;
    private final int WIDTH = 80;
    private final int HEIGHT = 50;
    private boolean alive = true;
    private static double globalVx = 65;
    private static boolean shouldMoveDown = false;
    private static boolean boundaryHitThisFrame = false;
    private  double movementBaseSpeed;
    private int wave;
    private  int aliveEnemyCount;
    private final long SHOOT_DELAY;
    private final List<Bullet> enemyBullet;
    private final double shootChance;

    // Ограничение спуска (макс. Глубина)
    private static final double MAX_DESCENT_Y = 700;

    public Enemy(double x, double y, long SHOOT_DELAY, double shootChance, List<Bullet> enemyBullet ) {
        this.x = x;
        this.y = y;
        this.SHOOT_DELAY = SHOOT_DELAY;
        this.shootChance = shootChance;
        this.enemyBullet = enemyBullet;
    }

    public void update(double dt, double worldW, int enemiesAliveCount, int wave) {
        if (!alive) return;
        //увеличение скорости если врагов <= 10
        double newGlobalVx = enemySpeed(enemiesAliveCount, wave);

        if (globalVx > 0) {
            globalVx = newGlobalVx;
        } else {
            globalVx = -newGlobalVx;
        }
        // Обычное горизонтальное движение
        x += globalVx * dt;

        //проверка на удар об границу экрана
        boolean hitBoundary = false;
        if (x < 20) {
            x = 20;
            hitBoundary = true;
        } else if (x + WIDTH > worldW - 20) {
            x = worldW - 20 - WIDTH;
            hitBoundary = true;
        }

        //смена направления врага при ударе об границу
        if (hitBoundary && !boundaryHitThisFrame) {
            boundaryHitThisFrame = true;
            shouldMoveDown = true;
            globalVx = -globalVx;
        }
    }


    // реализация стрельбы
    public void shoot(int aliveEnemiesCount) {  // Добавляем параметр
        Color color = Color.RED; // цвет пули врага
        double bulletX = x + (double) WIDTH / 2; // пуля вылетает по центру модельки врага
        double bulletY = y + HEIGHT; // пуля вылетает с самой низкой точки врага

        // Базовая скорость + бонус когда врагов мало
        double baseSpeed = 350; //базовая скорость врага
        double bulletSpeedBonus = 2.4; // бонус скорости пули
        if (aliveEnemiesCount < 5 && aliveEnemiesCount > 1){
            baseSpeed = baseSpeed * bulletSpeedBonus; // увеличение скорости пули когда врагов 5
        } else if (aliveEnemiesCount == 1) { //увеличение скорости пули когда враг 1
            bulletSpeedBonus = 3.3;
            baseSpeed = baseSpeed * bulletSpeedBonus;
        }


        enemyBullet.add(new Bullet(bulletX, bulletY, baseSpeed, color));
    }

    // рендер пули
    public  void renderenemyBullet(GraphicsContext gc) {
        for (Bullet bullet : enemyBullet){
            bullet.render(gc);
        }
    }

    //скорость врага
    public static double enemySpeed(int enemiesAliveCount, int wave ){
        double baseSpeed = 65;
        if (wave >= 5){
            baseSpeed = 155;
        }
        double speedBonus = 2.5;
        double newGlobalVx = baseSpeed;
        if (enemiesAliveCount <= 10 && enemiesAliveCount > 1){
            newGlobalVx = baseSpeed * speedBonus;
        } else if (enemiesAliveCount == 1) {
            speedBonus = 4;
            newGlobalVx = baseSpeed * speedBonus;
        }

        return newGlobalVx;
    }


    // Централизованный спуск всех врагов
    public static void moveAllDown(List<Enemy> enemies) {
        if (!shouldMoveDown) return;

        double descentAmount = 25;
        for (Enemy enemy : enemies) {
            if (enemy.alive && enemy.y < MAX_DESCENT_Y) {
                enemy.y += descentAmount;
            }
        }
        shouldMoveDown = false;
        boundaryHitThisFrame = false;
    }

    //рендер врага
    public void renderEnemy(GraphicsContext g) {
        if (!alive) return;
        // вывод изображения противника
        g.drawImage(SPRITE, x, y, WIDTH, HEIGHT);
    }
    // возвращение всех переменных в исходное состояние для реализации увеличения волны
    public static void resetGlobalState(int currentWave) {
        double baseSpeed = 65;

        if (currentWave >= 5){
            baseSpeed = 100;
        }

        globalVx = baseSpeed;
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

    //проверка на попадание во врага
    public boolean collidesWith(Bullet Bullet) {
        if (!alive) return false;

        return Bullet.getX() >= x &&
                Bullet.getX() <= x + WIDTH &&
                Bullet.getY() >= y &&
                Bullet.getY() <= y + HEIGHT;
    }

}