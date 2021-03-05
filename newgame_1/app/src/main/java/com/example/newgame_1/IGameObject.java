package com.example.newgame_1;

import android.graphics.Bitmap;

public interface IGameObject {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    Bitmap getBitmap();
}