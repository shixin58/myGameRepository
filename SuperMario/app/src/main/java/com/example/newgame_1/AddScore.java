package com.example.newgame_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 2016/12/21 0021.
 */
public class AddScore implements IGameObject {
    public Bitmap score;
    public GameView gameView;
    public int x;
    public int y;
    public int startY, mode;

    public AddScore(int x, int y, int mode, GameView gameView) {
        this.gameView = gameView;
        this.x = x;
        this.y = y;
        this.startY = y;
        this.mode = mode;
        switch (mode) {
            case 1:
                score = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.plus_five);
                break;
            case 2:
            case 3:
                score = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.plus_three);
                break;
            case 4:
                score = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.plus_four);
                break;
        }
    }

    @Override
    public Bitmap getBitmap() {
        y -= 10;
        if (y <= startY - 150) {
//        	if(score!=null)
//        	score.recycle();
            gameView.mHeroes.remove(this);
            System.gc();
        }
        return score;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
