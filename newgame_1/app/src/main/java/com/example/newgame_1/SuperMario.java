package com.example.newgame_1;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SuperMario implements IGameObject {

    public static final int MAX_SPEED = 25;

    public int x, y, width, height, speed = MAX_SPEED, speedTime = 0, count = 0, stop = 0, mTimes = 0, speedDrop = 0;

    public int mMoveIdx = 0, mMoveSize = 2, mMoveFirst = 0;

    private Bitmap mSourceBm, bitmap, mDeadBm, cache;

    private GameView gameView;

    public boolean mIsJump = false, mIsMove = false, mIsStop = true, xDirectionFlag = true, IsAdd = true,
            yDirectionFlag = true, Isdo = false, mAlive = true, speedflag = true, test = false, stateYflag = true;

    private List<Bitmap> mSprites = new CopyOnWriteArrayList<>();

    public List<IGameObject> now = new CopyOnWriteArrayList<>();

    public SuperMario(int x, int y, GameView gameView) {
        this.gameView = gameView;
        this.mSourceBm = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.all_mario);
        this.mDeadBm = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.mario_dead);
        this.x = x;
        this.width = mSourceBm.getWidth() / 6;
        this.height = mSourceBm.getHeight();
        this.cache = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        this.y = y - height;

        // 向右静止
        mSprites.add(Bitmap.createBitmap(mSourceBm, 0, 0, width - 2, height));
        // 向右走
        mSprites.add(Bitmap.createBitmap(mSourceBm, width, 0, width, height));
        // 向左走
        mSprites.add(Bitmap.createBitmap(mSourceBm, width * 2, 0, width, height));
        // 向左静止
        mSprites.add(Bitmap.createBitmap(mSourceBm, width * 3 + 1, 0, width - 1, height));
        // 向右跳
        mSprites.add(Bitmap.createBitmap(mSourceBm, width * 4, 0, width, height));
        // 向左跳
        mSprites.add(Bitmap.createBitmap(mSourceBm, width * 5, 0, width, height));
    }

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
        if (!mAlive) {
            // 死亡
            bitmap = mDeadBm;
            mTimes++;
            if (mTimes >= MAX_SPEED) {
                if (speedflag) {
                    speed = MAX_SPEED;
                    speedflag = false;
                }
                if (stateYflag) {
                    // 向上减速
                    y -= speed;
                    speed--;
                    if (speed <= 0) {
                        speed = 0;
                        stateYflag = false;
                    }
                } else {
                    speed++;
                    y += speed;
                    if (y > gameView.mScreenHeight) {
                        gameView.mHeroes.remove(this);
                    }
                }
            }
        } else {
            if (mIsMove) {
                if (!mIsJump) {
                    bitmap = mSprites.get(mMoveIdx);
                    if (count == 5) {
                        mMoveIdx++;
                        if (mMoveIdx == mMoveSize) {
                            mMoveIdx = mMoveFirst;
                        }
                        count = 0;
                    }
                    count++;
                }
                stop++;
                if (stop == 1) {
                    if (xDirectionFlag) {
                        if (x <= gameView.mScreenWidth - width) {
                            x += 5;
                        }
                    } else {
                        if (x > 0) {
                            x -= 5;
                        }
                    }
                    stop = 0;
                }
            } else {
                // 静止
                if (xDirectionFlag) {
                    // 向右
                    bitmap = mSprites.get(0);
                } else {
                    bitmap = mSprites.get(3);
                }
            }
            if (Isdo) {
                if (!mIsJump) {
                    if (speedDrop < MAX_SPEED) {
                        speedDrop++;
                    }
                    speedTime++;
                    y += speedDrop;
                    if (y > gameView.mScreenHeight) {
                        gameView.mHeroes.remove(this);
                    }
                }
            }
            if (!mIsJump) {
                if (now.size() == 1) {
                    if (now.get(0) instanceof BombA) {
                        BombA bombA = (BombA) now.get(0);
                        switch (bombA.mode) {
                            case 1:
                                setY(now.get(0).getY() - height);
                                if (x > 0) {
                                    x -= bombA.speed;
                                }
                                break;
                            case 2:
                                setY(now.get(0).getY() - height);
                                if (x < gameView.mScreenWidth - width) {
                                    x += bombA.speed;
                                }
                                break;
                        }
                    } else {
                        Road road = (Road) now.get(0);
                        if (road.mode == 1) {
                            Isdo = false;
                            if (!mIsJump) {
                                setY(now.get(0).getY() - height);
                            }
                        } else {
                            setY(now.get(0).getY() - height);
                        }
                    }
                }
            }
            if (mIsStop) {
                mIsJump = false;
                speed = MAX_SPEED;
                yDirectionFlag = true;
            }
        }
        return bitmap;
    }

    public boolean isCollisionWithRect(int x1, int y1, int w1, int h1,
                                       int x2, int y2, int w2, int h2) {
        return x1 + w1 >= x2 + 10 && y1 + h1 >= y2 && x1 <= x2 + w2 - 10 && y1 + h1 / 2 <= y2;
    }

    public boolean isCollisionWithRect2(int x1, int y1, int w1, int h1,
                                        int x2, int y2, int w2, int h2) {
        return x1 + w1 >= x2 && y1 + h1 >= y2 && x1 <= x2 + w2 && y1 <= y2 + h2;
    }

    public void IsImpact(List<IGameObject> props) {
        if (mAlive) {
            if (mIsJump) {
                if (xDirectionFlag) {
                    // 向右
                    bitmap = mSprites.get(4);
                } else {
                    bitmap = mSprites.get(5);
                }
                if (yDirectionFlag) {
                    // 向上
                    y -= speed;
                    speed--;
                    if (speed <= 0) {
                        speed = 0;
                        yDirectionFlag = false;
                    }
                } else {
                    if (speed <= 15) {
                        speed++;
                    }
                    y += speed;
                    if (y > gameView.mScreenHeight) {
                        gameView.mHeroes.remove(this);
                    }
                }
            }

            for (IGameObject prop : props) {
                if (prop instanceof Road) {
                    Road road = (Road) prop;
                    if (isCollisionWithRect(x, y, width, height, road.getX(), road.getY(), road.getWidth(), road.getHeight())) {
                        now.clear();
                        now.add(road);
                    } else {
                        switch (road.mode) {
                            case 0:
                                if (now.size() == 1) {
                                    now.remove(road);
                                }
                            case 2:
                                if (now.size() == 1) {
                                    now.remove(road);
                                }
                                if (!mIsJump) {
                                    Isdo = true;
                                }
                            case 3:
                                if (now.size() == 1) {
                                    now.remove(road);
                                }
                                break;
                        }
                    }
                }
            }

            if (now.size() == 1) {
                IGameObject prop = now.get(0);
                if (isCollisionWithRect2(x, y, width, height, prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight())) {
                    Isdo = false;
                    speedDrop = 0;
                    mIsStop = true;
                }
            }
        }
    }
}
