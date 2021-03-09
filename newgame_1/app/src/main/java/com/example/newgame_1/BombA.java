package com.example.newgame_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BombA implements IGameObject {
    public int x, y, width, height, mode, speed, deadspeed = 12;
    private Bitmap bitmap;
    private GameView gameView;
    private boolean IsDoT = false, IsDoB = false, IsDo = false, state = true, deaddirection = true;

    public BombA(int x, int y, int mode, GameView gameView) {
        this.gameView = gameView;
        switch (mode) {
            case 1:
                this.bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bullet_1);
                break;
            case 2:
                this.bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bullet_2);
                break;
            case 3:
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
            case 1:
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
            case 2:
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
            case 3:
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
            IsDoT = true;
            IsDoB = false;
        } else if (y1 > y2 + h2) {
            IsDoB = true;
            IsDoT = false;
        } else if (x1 + w1 < x2 || x1 > x2 + w2) {
            if (y1 + h1 != y2) {
                IsDoB = false;
                IsDoT = false;
                IsDo = true;
            }
        }

        final boolean b = x1 + w1 >= x2 + 15 && y1 + h1 >= y2 && x1 <= x2 + w2 - 15 && y1 <= y2 + h2;
        if (IsDoT) {
            if (b) {
                return 1;
            }
        }
        if (IsDoB) {
            if (b) {
                return 2;
            }
        }
        if (IsDo) {
            if (b) {
                return 3;
            }
        }
        return 0;
    }

    public boolean isCollisionWithRect2(int x1, int y1, int w1, int h1,
                                        int x2, int y2, int w2, int h2) {
        return x1 + w1 >= x2 + 15 && y1 + h1 >= y2 && x1 <= x2 + w2 - 15 && y1 <= y2 + h2;
    }

    public void IsImpact(SuperMario hero) {
        switch (mode) {
            case 1:
            case 2:
                switch (isCollisionWithRect(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
                    case 0:
                        if (hero.now.size() == 1) {
                            hero.now.remove(this);
                        }
                        break;
                    case 1:
                        if (hero.now.size() != 0) {
                            if (hero.now.get(0) instanceof Road) {
                                Road road = (Road) hero.now.get(0);
                                if (road.mode != 3 && state)
                                    hero.mAlive = false;
                            } else {
                                hero.now.clear();
                            }
                        }
                        if (hero.now.size() == 0) {
                            if (state)
                                hero.now.add(this);
                        }
                        break;
                    case 2:
                        state = false;
                        if (hero.now.size() == 1) {
                            hero.now.remove(this);
                        }
                        break;
                    case 3:
                        hero.mAlive = false;
                        break;
                }
                break;
            case 3:
                if (isCollisionWithRect2(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
                    hero.mAlive = false;
                }
                break;
        }
    }
}
