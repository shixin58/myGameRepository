package com.example.newgame_1;

import android.graphics.Bitmap;

public class Road implements IGameObject {

    public static final int MODE_STILL = 0;
    public static final int MODE_MOVE_VERTICAL = 1;
    public static final int MODE_MOVE_HORIZONTAL = 2;
    public static final int MODE_MOVE_HORIZONTAL_TURRET = 3;

    public int x, y, width, height, mode, speed = 7, startY,
            startX, time = 0, addtime = 0, limittime = 200;
    private Bitmap bitmap;
    private GameView gameView;
    public boolean Isdo = false, direction/*向左or向上*/ = true, state = true;

    public Road(Bitmap bitmap, int x, int y, int mode, GameView gameView) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.mode = mode;
        this.gameView = gameView;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Bitmap getBitmap() {
        switch (mode) {
            case MODE_MOVE_VERTICAL:
                if (direction) {
                    // 向上减速
                    time++;
                    if (time >= 5) {
                        speed--;
                        time = 0;
                    }
                    y -= speed;
                    if (speed <= 0) {
                        speed = 0;
                        direction = false;
                    }
                } else {
                    // 向下加速
                    time++;
                    if (time >= 5) {
                        if (speed < 7)
                            speed++;
                        time = 0;
                    }
                    y += speed;
                    if (y >= startY) {
                        speed = 7;
                        direction = true;
                    }
                }
                break;
            case MODE_MOVE_HORIZONTAL:
                if (direction) {
                    // 向左减速
                    time++;
                    if (time >= 10) {
                        speed--;
                        time = 0;
                    }
                    x -= speed;
                    if (speed <= 0) {
                        speed = 7;
                        direction = false;
                    }
                } else {
                    // 向右加速
                    time++;
                    if (time >= 10) {
                        if (speed < 7)
                            speed++;
                        time = 0;
                    }
                    x += speed;
                    if (x >= startX) {
                        speed = 7;
                        direction = true;
                    }
                }
                break;
            case MODE_MOVE_HORIZONTAL_TURRET:
                if (direction) {
                    // 向左减速
                    time++;
                    if (time >= 10) {
                        speed--;
                        time = 0;
                    }
                    x -= speed;
                    if (speed <= 0) {
                        speed = 7;
                        direction = false;
                    }
                } else {
                    // 向右加速
                    time++;
                    if (time >= 10) {
                        if (speed < 7)
                            speed++;
                        time = 0;
                    }
                    x += speed;
                    if (x >= startX) {
                        speed = 7;
                        direction = true;
                    }
                }
                BombA bombA;
                addtime++;
                if (addtime >= limittime) {
                    int var = gameView.random.nextInt(30);
                    int nowMode;
                    int nowX;
                    if (var <= 10) {
                        nowMode = 1;
                        nowX = x - gameView.mBullet_1.getWidth();
                    } else if (var <= 20) {
                        nowMode = 2;
                        nowX = x + width;
                    } else {
                        nowMode = 3;
                        nowX = x - gameView.mBullet_1.getWidth();
                    }
                    bombA = new BombA(nowX, y + 5, nowMode, gameView);
                    gameView.monsters.add(bombA);
                    limittime = gameView.random.nextInt(280) + 320;
                    addtime = 0;
                }
                break;
        }
        return bitmap;
    }
}
