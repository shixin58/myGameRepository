package com.example.newgame_1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnTouchListener {

    private ImageButton left = null, right = null, jump = null;
    public GameView gameView = null;
    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        instance = this;
        setContentView(R.layout.activity_main);
        init();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            gameView.stop();
            Intent intenttwo = new Intent(MainActivity.this, ExitActivity.class);
            intenttwo.putExtra("TagText", "main");
            startActivity(intenttwo);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void init() {
        gameView = (GameView) findViewById(R.id.test);
        left = (ImageButton) findViewById(R.id.left);
        right = (ImageButton) findViewById(R.id.right);
        jump = (ImageButton) findViewById(R.id.jump);
        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
        jump.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.left:
                        gameView.mMario.mIsMove = true;
                        gameView.mMario.xDirectionFlag = false;
                        gameView.mMario.mMoveFirst = 2;
                        gameView.mMario.mMoveIdx = gameView.mMario.mMoveFirst;
                        gameView.mMario.mMoveSize = 4;
                        left.setBackgroundResource(R.drawable.key_left_red);
                        break;
                    case R.id.right:
                        gameView.mMario.mIsMove = true;
                        gameView.mMario.xDirectionFlag = true;
                        gameView.mMario.mMoveFirst = 0;
                        gameView.mMario.mMoveIdx = gameView.mMario.mMoveFirst;
                        gameView.mMario.mMoveSize = 2;
                        right.setBackgroundResource(R.drawable.key_right_red);
                        break;
                    case R.id.jump:
                        if (!gameView.mMario.mIsJump && gameView.mMario.mAlive && !gameView.mMario.Isdo) {
                            gameView.mMario.Isdo = false;
                            gameView.mMario.mIsStop = false;
                            gameView.mMario.mIsJump = true;
                            jump.setBackgroundResource(R.drawable.key_jump_red);
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.left:
                        left.setBackgroundResource(R.drawable.key_left_blue);
                        gameView.mMario.mIsMove = false;
                        break;
                    case R.id.right:
                        right.setBackgroundResource(R.drawable.key_right_blue);
                        gameView.mMario.mIsMove = false;
                        break;
                    case R.id.jump:
                        jump.setBackgroundResource(R.drawable.key_jump_blue);
                        break;
                }
                break;
        }
        return true;
    }
}
