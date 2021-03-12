package com.example.newgame_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BombA implements IGameObject {

    public static final int MODE_LEFT_BLACK = 1;
    public static final int MODE_RIGHT_BLACK = 2;
    public static final int MODE_LEFT_ORANGE = 3;

    public static final int IMPACT_NONE = 0;
    public static final int IMPACT_TOP = 1;
    public static final int IMPACT_BOTTOM = 2;
    public static final int IMPACT_MIDDLE = 3;

    public int x, y, width, height, mode, speed, deadspeed = 12;
    private Bitmap bitmap;
    private GameView gameView;
    private boolean IsDoT = false, IsDoB = false, IsDo = false, state = true, deaddirection = true;

    public BombA(int x, int y, int mode, GameView gameView) {
        this.gameView = gameView;
        switch (mode) {
            case MODE_LEFT_BLACK:
                this.bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bullet_1);
                break;
            case MODE_RIGHT_BLACK:
                this.bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bullet_2);
                break;
            case MODE_LEFT_ORANGE:
                this.bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bullet_3);
                break;
        }

        this.x = x;
        this.y = y;
        this.mode = mode;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        //this.speed=random.nextInt(6)+4;
        this.speed = gameView.random.nextInt(5) + 1;
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
            case MODE_LEFT_BLACK:
                if (state) {
                    x -= speed;
                    if (x <= -width)
                        gameView.monsters.remove(this);

                } else {
                    x -= 5;
                    if (deaddirection) {
                        deadspeed--;
                        y -= deadspeed;
                        if (deadspeed <= 0) {
                            deadspeed = 0;
                            deaddirection = false;
                        }
                    } else {
                        deadspeed++;
                        y += deadspeed;
                        if (y >= gameView.mScreenHeight)
                            gameView.monsters.remove(this);
                    }
                }
                break;
            case MODE_RIGHT_BLACK:
                if (state) {
                    x += speed;
                    if (x >= gameView.mScreenWidth)
                        gameView.monsters.remove(this);
                } else {
                    x += 5;
                    if (deaddirection) {
                        deadspeed--;
                        y -= deadspeed;
                        if (deadspeed <= 0) {
                            deadspeed = 0;
                            deaddirection = false;
                        }
                    } else {
                        deadspeed++;
                        y += deadspeed;
                        if (y >= gameView.mScreenHeight)
                            gameView.monsters.remove(this);
                    }
                }
                break;
            case MODE_LEFT_ORANGE:
                if (state) {
                    x -= speed;
                    if (x >= gameView.mScreenWidth)
                        gameView.monsters.remove(this);
                } else {
                    x -= 5;
                    if (deaddirection) {
                        deadspeed--;
                        y -= deadspeed;
                        if (deadspeed <= 0) {
                            deadspeed = 0;
                            deaddirection = false;
                        }
                    } else {
                        deadspeed++;
                        y += deadspeed;
                        if (y >= gameView.mScreenHeight)
                            gameView.monsters.remove(this);
                    }
                }
                break;
        }
        return bitmap;
    }

    public int isCollisionWithRect(int x1, int y1, int w1, int h1,
                                   int x2, int y2, int w2, int h2) {
        if (y1 + h1 < y2) {
            // Mario在子弹上边
            IsDoT = true;
            IsDoB = false;
        } else if (y1 > y2 + h2) {
            // 子弹下边
            IsDoB = true;
            IsDoT = false;
        } else if (x1 + w1 < x2 || x1 > x2 + w2) {
            // 子弹左边或右边
            if (y1 + h1 != y2 && y1 != y2 + h2) {
                IsDoB = false;
                IsDoT = false;
                IsDo = true;
            }
        }

        final boolean b = isCollisionWithRect2(x1, y1, w1, h1, x2, y2, w2, h2);
        if (IsDoT && b) {
            return IMPACT_TOP;
        }
        if (IsDoB && b) {
            return IMPACT_BOTTOM;
        }
        if (IsDo && b) {
            return IMPACT_MIDDLE;
        }
        return IMPACT_NONE;
    }

    public boolean isCollisionWithRect2(int x1, int y1, int w1, int h1,
                                        int x2, int y2, int w2, int h2) {
        return x1 + w1 >= x2 + 15 && y1 + h1 >= y2 && x1 <= x2 + w2 - 15 && y1 <= y2 + h2;
    }

    public void IsImpact(SuperMario hero) {
        switch (mode) {
            case MODE_LEFT_BLACK:
            case MODE_RIGHT_BLACK:
                switch (isCollisionWithRect(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
                    case IMPACT_NONE:
                        if (hero.now.size() == 1) {
                            hero.now.remove(this);
                        }
                        break;
                    case IMPACT_TOP:
                        if (hero.now.size() != 0) {
                            if (hero.now.get(0) instanceof Road) {
                                Road road = (Road) hero.now.get(0);
                                if (road.mode != Road.MODE_MOVE_HORIZONTAL_TURRET && state) {
                                    hero.mAlive = false;
                                }
                            } else {
                                hero.now.clear();
                            }
                        }
                        if (hero.now.size() == 0) {
                            if (state)
                                hero.now.add(this);
                        }
                        break;
                    case IMPACT_BOTTOM:
                        state = false;
                        if (hero.now.size() == 1) {
                            hero.now.remove(this);
                        }
                        break;
                    case IMPACT_MIDDLE:
                        hero.mAlive = false;
                        break;
                }
                break;
            case MODE_LEFT_ORANGE:
                if (isCollisionWithRect2(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
                    hero.mAlive = false;
                }
                break;
        }
    }
}
