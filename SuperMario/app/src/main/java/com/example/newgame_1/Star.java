package com.example.newgame_1;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;

public class Star implements IGameObject {
    private int x, y, model, width, height, degrees = 0, speedX = 0, speedY = 0, time = 0, limmittime = 50;
    private GameView gameView;
    private Bitmap cache, bitmap;
    private Canvas canvas;
    private boolean directionX = true, IsStop = false;

    public Star(int x, int y, int model, GameView gameView) {
        this.x = x;
        this.y = y;
        this.model = model;
        this.gameView = gameView;
        switch (model) {
            case 1:
                if (gameView.random.nextInt(30) <= 10) {
                    bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.star_blue);
                } else if (gameView.random.nextInt(30) > 10 && gameView.random.nextInt(30) <= 20) {
                    bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.star_green);
                } else {
                    bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.star_my);
                }
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.star_red);
                break;
        }
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        if (gameView.random.nextInt(10) >= 5) {
            directionX = true;
        } else {
            directionX = false;
        }
        speedX = gameView.random.nextInt(4);
        cache = Bitmap.createBitmap(width * 3 / 2, height * 3 / 2, Config.ARGB_8888);
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
        switch (model) {
            case 1:
                if (!IsStop) {
                    if (speedY < 10)
                        speedY++;
                    y += speedY;
                    if (directionX) {
                        x -= speedX;
                    } else {
                        x += speedX;
                    }

                    degrees += 3;
                    if (degrees >= 360) {
                        degrees = 0;
                    }
                    canvas = new Canvas(cache);
                    canvas.save();
                    canvas.rotate(degrees, width * 3 / 4, height * 3 / 4);
                    canvas.translate(width / 4, height / 4);
                    canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    canvas.restore();
                }
                if (x >= gameView.mScreenWidth || x <= -width || y >= gameView.mScreenHeight) {
                    IsStop = true;
                    time++;
                    if (time >= limmittime) {
                        if (gameView.random.nextInt(30) <= 10) {
                            bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.star_blue);
                        } else if (gameView.random.nextInt(30) > 10 && gameView.random.nextInt(30) <= 20) {
                            bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.star_green);
                        } else {
                            bitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.star_my);
                        }
                        limmittime = gameView.random.nextInt(80) + 80;
                        x = gameView.random.nextInt(gameView.mScreenWidth - 200) + 100;
                        y = -100;
                        IsStop = false;
                        time = 0;
                    }
                }
                break;
        }
        return cache;
    }
}
