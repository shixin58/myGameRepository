package com.example.newgame_1;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Bitmap;

public class Tortoise implements IGameObject {
    public int x, y, width, height, mode, state = 1, count = 0, index = 0, size = 2, first = 0, speedX, speedY = 0, time = 0, timeTwo = 0, dropSpeed = 10;
    public Bitmap bitmap, dead;
    public Random random = new Random();
    public GameView gameView;
    public boolean directionX = true, IsOnRoad = false, IsMove = false, Isdo = false, sleepdirect, firstDo = false, dropYdirection = true;
    public List<Bitmap> sprites = new CopyOnWriteArrayList<>();
    public List<IGameObject> now = new CopyOnWriteArrayList<>();

    public Tortoise(Bitmap bitmap, Bitmap dead, int x, int y, int mode, int speed, GameView gameView) {
        this.gameView = gameView;
        this.x = x;
        this.y = y;
        this.mode = mode;
        this.speedX = speed;
        this.bitmap = bitmap;
        this.width = bitmap.getWidth() / 5;
        this.height = bitmap.getHeight();
        this.dead = dead;
        if (random.nextInt(100) > 50) {
            directionX = true;
            index = 0;
            size = 2;
            first = 0;
        } else {
            directionX = false;
            index = 2;
            size = 4;
            first = 2;
        }

        sprites.add(Bitmap.createBitmap(bitmap, 0, 0, width, height));
        sprites.add(Bitmap.createBitmap(bitmap, width, 0, width, height));
        sprites.add(Bitmap.createBitmap(bitmap, 2 * width, 0, width, height));
        sprites.add(Bitmap.createBitmap(bitmap, 3 * width, 0, width, height));
        sprites.add(Bitmap.createBitmap(bitmap, 4 * width, 0, width, height));
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
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
        Bitmap map = null;
        if (!IsOnRoad) {
            if (state != 3) {
                if (speedY < 10) {
                    speedY++;
                }
                y += speedY;
                if (y >= gameView.mScreenHeight)
                    gameView.monsters.remove(this);
            }
        }
        if (now.size() == 1) {
            Road road = (Road) now.get(0);
            if (state != 3)
                setY(road.getY() - height);
//			if(mode==1){
//				startX=road.getX();
//				limmitW=road.getWidth();
//			}
        }
        switch (state) {
            case 1:
                switch (mode) {
                    case 1:
                        if (directionX) {
                            if (x >= gameView.mScreenWidth - width) {
                                first = 2;
                                index = first;
                                size = 4;
                                directionX = false;
                            } else {
                                x += speedX;
                            }
                        } else {
                            if (x <= 0) {
                                first = 0;
                                index = first;
                                size = 2;
                                directionX = true;
                            } else {
                                x -= speedX;
                            }
                        }
                        break;
                }

                map = sprites.get(index);
                if (count == 8) {
                    index++;
                    if (index == size) {
                        index = first;
                    }
                    count = 0;
                }
                count++;
                break;
            case 2:
                if (IsMove) {
                    if (sleepdirect) {
                        x += 12;
                        if (x >= gameView.mScreenWidth - width) {
                            sleepdirect = false;
                        }
                    } else {
                        x -= 12;
                        if (x <= 0) {
                            sleepdirect = true;
                        }
                    }
                } else {
                    time++;
                    if (time >= 300) {
                        state = 1;
                        IsMove = false;
                        time = 0;
                    }
                }
                if (x >= gameView.mScreenWidth || x <= -width) {
                    gameView.monsters.remove(this);
                }
                map = sprites.get(4);
                break;
            case 3:
                map = dead;
                if (dropYdirection) {
                    if (dropSpeed > 0) {
                        dropSpeed--;
                    } else {
                        dropYdirection = false;
                    }
                    y -= dropSpeed;
                } else {
                    if (dropSpeed < 20)
                        dropSpeed++;
                    y += dropSpeed;
                }
                if (x <= -width || x >= gameView.mScreenWidth || y >= gameView.mScreenHeight)
                    gameView.monsters.remove(this);
                break;
        }

        return map;
    }

    public boolean isCollisionWithRect3(int x1, int y1, int w1, int h1,
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
            if (x1 + w1 >= x2 && y1 + h1 >= y2 && x1 <= x2 + w2 && y1 <= y2 + h2) {
                if (state == 2) {
                    if (x1 + w1 / 2 > x2 + w2 / 2) {
                        sleepdirect = false;
                        if (!IsMove)
                            setX(x2 - w1);
                    } else {
                        sleepdirect = true;
                        if (!IsMove)
                            setX(x2 + w2);
                    }
                }
                return 1;
            }
        }
        return 0;
    }

    public boolean isCollisionWithRect(int x1, int y1, int w1, int h1,
                                       int x2, int y2, int w2, int h2) {
        return x1 + w1 >= x2 + 10 && y1 + h1 >= y2 && x1 <= x2 + w2 - 10 && y1 + h1 * 3 / 4 <= y2;
    }

    public void IsImpact(SuperMario hero) {
        if (state == 2) {
            if (hero.mIsJump && !hero.Isdo) {
                if (!hero.yDirectionFlag) {
                    switch (isCollisionWithRect2(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
                        case 1:
                            if (!IsMove) {
                                IsMove = true;
                            } else {
                                hero.yDirectionFlag = true;
                                hero.speed = 12;
                                IsMove = false;
                                time = 0;
                            }
                            break;
                    }
                }
            } else if (!hero.mIsJump && !hero.Isdo) {
                if (state == 1) {
                    switch (isCollisionWithRect2(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
                        case 1:
                            state = 2;
                            break;
                        case 2:
                            hero.mAlive = false;
                            break;
                    }
                } else if (state == 2) {
                    if (IsMove) {
                        switch (isCollisionWithRect2(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
                            case 1:
                                IsMove = false;
                                break;
                            case 2:
                                hero.mAlive = false;
                                break;
                        }
                    } else {
                        switch (isCollisionWithRect2(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
                            case 1:
                                hero.yDirectionFlag = true;
                                hero.speed = 12;
                                hero.mIsJump = true;
                                IsMove = true;
                                break;
                        }
                    }
                }
            }
        } else if (state == 1) {
            switch (isCollisionWithRect2(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight(), x, y, width, height)) {
                case 1:
                    if (!hero.yDirectionFlag) {
                        hero.yDirectionFlag = true;
                        hero.speed = 12;
                        state = 2;
                    }
                    break;
                case 2:
                    hero.mAlive = false;
                    break;
            }
        }
    }

    public void IsImpactTortoise(List<IGameObject> bitmaps) {
        for (IGameObject obj : bitmaps) {
            if (obj instanceof Tortoise) {
                Tortoise tortoise = (Tortoise) obj;
                if (tortoise.state == 2 && tortoise.IsMove) {
                    if (tortoise.hashCode() != this.hashCode()) {
                        if (isCollisionWithRect3(x, y, width, height, tortoise.getX(), tortoise.getY(), tortoise.getWidth(), tortoise.getHeight())) {
                            state = 3;
                        }
                    }
                }
            }
        }
    }

    public void IsImpactRoad(List<IGameObject> bitmaps) {
        for (IGameObject obj : bitmaps) {
            if (obj instanceof Road) {
                Road road = (Road) obj;
                if (road.mode != Road.MODE_MOVE_HORIZONTAL_TURRET) {
                    if (isCollisionWithRect(x, y, width, height, road.getX(), road.getY(), road.getWidth(), road.getHeight())) {
                        now.clear();
                        if (state != 3) {
                            now.add(road);
                        }
                    } else {
                        switch (road.mode) {
                            case Road.MODE_STILL:
                                if (now.size() == 1) {
                                    now.remove(road);
                                }
                                break;
                            case Road.MODE_MOVE_HORIZONTAL:
                                if (now.size() == 1) {
                                    now.remove(road);
                                }
                                IsOnRoad = false;
                        }

                    }
                }
            }
        }
        if (now.size() == 1) {
            if (isCollisionWithRect3(x, y, width, height, now.get(0).getX(), now.get(0).getY(), now.get(0).getWidth(), now.get(0).getHeight())) {
                IsOnRoad = true;
            }
        }
    }
}
