package edu.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


import java.util.*;


public class Enemy{
    private static final Image SPRITE = new Image(
            Objects.requireNonNull(Enemy.class.getResource("/Models/Alien.png")).toString()
    );
    private double x;
    private double y;
    private final int WIDTH = 80;
    private final int HEIGHT = 50;
    private boolean alive = true;
    private static double globalVx = 80;
    private static boolean shouldMoveDown = false;
    private static boolean boundaryHitThisFrame = false;
    private static long lastEnemyShotTime = 0;
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

        double currentSpeed = globalVx > 0 ?
                Math.abs(globalVx) :
                -Math.abs(globalVx);
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
    public static void updateEnemyShooting(long now, List<Enemy> enemies, int wave) {
        // 3 секунды между выстрелами врагов
        long ENEMY_SHOOT_INTERVAL = 5_000_000_000L;
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

            if (wave >= 5 ) MAX_SHOOTING_ENEMIES = 8;
            int enemiesToShoot = Math.min(MAX_SHOOTING_ENEMIES, availableEnemies.size());

            for (int i = 0; i < enemiesToShoot; i++) {
                availableEnemies.get(i).shoot(aliveEnemiesCount);
            }

            lastEnemyShotTime = now;
        }
    }


    // реализация стрельбы
    public void shoot(int aliveEnemiesCount) {
        double baseSpeed = 325;
        Color color = Color.RED; // цвет пули врага
        double bulletX = x + (double) WIDTH / 2; // пуля вылетает по центру модельки врага
        double bulletY = y + HEIGHT; // пуля вылетает с самой низкой точки врага

        // Базовая скорость + бонус когда врагов мало
        double bulletSpeedBonus = 1.0; // бонус скорости пули
        if (aliveEnemiesCount < 11 && aliveEnemiesCount > 3){
             bulletSpeedBonus = 1.5;// увеличение скорости пули когда врагов 5
        } else if (aliveEnemiesCount <= 3 && aliveEnemiesCount > 1) { //увеличение скорости пули когда враг 1
            bulletSpeedBonus = 2.0;
        } else if ( aliveEnemiesCount == 1) {
            bulletSpeedBonus = 2.2;
        }
        
        double newSpeed = baseSpeed * bulletSpeedBonus;

        enemyBullet.add(new Bullet(bulletX, bulletY, newSpeed, color));
    }

    // рендер пули
    public  void renderenemyBullet(GraphicsContext gc) {
        for (Bullet bullet : enemyBullet){
            bullet.render(gc);
        }
    }

    //скорость врага
    public static double enemySpeed(int enemiesAliveCount, int wave ){
        double baseSpeed = 80;
        if (wave >= 5){
            baseSpeed = 125;
        }
        double speedBonus = 1.0;
        if (enemiesAliveCount <= 10 && enemiesAliveCount > 3){
            speedBonus = 2.3;
        } else if (enemiesAliveCount <= 3 && enemiesAliveCount > 1) {
            speedBonus = 3.2;
        }else if (enemiesAliveCount == 1){
            speedBonus = 4;
        }

        double newSpeed = baseSpeed * speedBonus;
        globalVx = globalVx > 0 ? newSpeed : -newSpeed;

        return globalVx;
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

    public static int[] checkCollisionsEnemy(List<Enemy> enemies, List<Bullet> playerBullets, int currentWave, int currentScore, Runnable callBack) {
        int score = currentScore;
        int wave = currentWave;

        Iterator<Bullet> bulletIterator = playerBullets.iterator();

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
        enemies.removeIf(enemy -> !enemy.isAlive());


        if (enemies.isEmpty()) {
            wave++;
            score += 500;
            callBack.run();
        }

      return  new int[]{score, wave};
    }

    //рендер врага
    public void renderEnemy(GraphicsContext g) {
        if (!alive) return;
        // вывод изображения противника
        g.drawImage(SPRITE, x, y, WIDTH, HEIGHT);
    }
    // возвращение всех переменных в исходное состояние для реализации увеличения волны
    public static void resetGlobalState(int currentWave) {
        double baseSpeed = 80;

        if (currentWave >= 5){
            baseSpeed = 125;
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