package com.example.newgame_1;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlyMonster implements IGameObject {
    private int x, y, mode, width, height, speedY = 15, speed = 0, index = 0, count = 0;
    private Bitmap bitmap;
    private GameView gameView;
    private boolean directionY = true, directionX = true, IsDo = false, Isdo = false, IsJump = false, IsDead = false, IsAdd = true;
    private List<Bitmap> sprites = new CopyOnWriteArrayList<>();

    public FlyMonster(Bitmap bitmap, int x, int y, int mode, GameView gameView) {
        this.x = x;
        this.y = y;
        this.mode = mode;
        this.gameView = gameView;
        this.bitmap = bitmap;
        switch (mode) {
            case 1:
                this.width = bitmap.getWidth() / 5;
                this.height = bitmap.getHeight();
                sprites.add(Bitmap.createBitmap(bitmap, 0, 0, width, height));
                sprites.add(Bitmap.createBitmap(bitmap, width, 0, width, height));
                sprites.add(Bitmap.createBitmap(bitmap, 2 * width, 0, width, height));
                sprites.add(Bitmap.createBitmap(bitmap, 3 * width, 0, width, height));
                sprites.add(Bitmap.createBitmap(bitmap, 4 * width, 0, width, height));
                break;
        }
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
        if (y >= gameView.mScreenHeight || x >= gameView.mScreenWidth || x <= -width)
            gameView.monsters.remove(this);
        bitmap = sprites.get(index);
        if (count == 8) {
            index++;
            if (index == sprites.size() - 1) {
                index = 0;
            }
            count = 0;
        }
        count++;
        if (IsJump) {
            if (directionY) {
                speedY--;
                y -= speedY;
                if (speedY < 0) {
                    speedY = 0;
                    directionY = false;
                }
            } else {
                if (speedY < 15)
                    speedY++;
                y += speedY;

            }
        }
        if (IsDo) {
            if (!IsJump) {
                if (speed < 15)
                    speed++;
                y += speed;
                if (y >= gameView.mScreenHeight)
                    gameView.monsters.remove(this);
            }

        }
        if (IsDead) {
            if (IsAdd) {
                int varB = gameView.random.nextInt(40);
                int model = 0;
                if (varB <= 10) {
                    model = 1;
                } else if (varB > 10 && varB <= 20) {
                    model = 2;
                } else if (varB > 20 && varB <= 30) {
                    model = 3;
                } else {
                    model = 4;
                }
                Monster monster = new Monster(x, y + 20, model, gameView);
                gameView.monsters.add(monster);
                IsAdd = false;
            }
            gameView.monsters.remove(this);
        }
        return bitmap;
    }

    public boolean isCollisionWithRect(int x1, int y1, int w1, int h1,
                                       int x2, int y2, int w2, int h2) {
        return x1 + w1 >= x2 + 15 && y1 + h1 >= y2 && x1 <= x2 + w2 - 15 && y1 <= y2 + h2;
    }

    public int isCollisionWithRect2(int x1, int y1, int w1, int h1,
                                    int x2, int y2, int w2, int h2) {
        if (y1 + h1 < y2 + h2 / 2) {
            Isdo = true;
        } else {
            Isdo = false;
            if (x1 + w1 >= x2 + 10 && y1 + h1 >= y2 && x1 <= x2 + w2 - 10 && y1 <= y2 + h2) {
                return 2;
            }
        }
        if (Isdo) {
            if (x1 + w1 >= x2 + 15 && y1 + h1 >= y2 && x1 <= x2 + w2 - 15 && y1 <= y2 + h2) {
                return 1;
            }
        }
        return 0;
    }

    public void IsImpact(SuperMario hero) {
        switch (isCollisionWithRect2(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
            case 1:
                if (!hero.yDirectionFlag) {
                    if (!IsDead) {
                        hero.yDirectionFlag = true;
                        hero.speed = 10;
                    }
                    IsDead = true;
                }
                break;
            case 2:
                if (!IsDead)
                    hero.mAlive = false;
                break;
        }
    }

    public void IsImpactRoad(List<IGameObject> bitmaps) {
        for (IGameObject obj : bitmaps) {
            if (obj instanceof Road) {
                Road road = (Road) obj;
                if (isCollisionWithRect(x, y, width, height, road.getX(), road.getY(), road.getWidth(), road.getHeight())) {
                    IsJump = true;
                    speedY = 15;
                    directionY = true;
                } else {
                    IsDo = true;
                }
            }
        }
    }
}
