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

    private ImageButton mLeftBtn = null, mRightBtn = null, mJumpBtn = null;
    public GameView mGameView = null;
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
            mGameView.stop();
            Intent exitIntent = new Intent(MainActivity.this, ExitActivity.class);
            exitIntent.putExtra("TagText", "main");
            startActivity(exitIntent);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void init() {
        mGameView = (GameView) findViewById(R.id.test);
        mLeftBtn = (ImageButton) findViewById(R.id.left);
        mRightBtn = (ImageButton) findViewById(R.id.right);
        mJumpBtn = (ImageButton) findViewById(R.id.jump);
        mLeftBtn.setOnTouchListener(this);
        mRightBtn.setOnTouchListener(this);
        mJumpBtn.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.left:
                        mGameView.mMario.mIsMove = true;
                        mGameView.mMario.xDirectionFlag = false;
                        mGameView.mMario.mMoveIdx = mGameView.mMario.mMoveFirst = 2;
                        mGameView.mMario.mMoveSize = 4;
                        mLeftBtn.setBackgroundResource(R.drawable.key_left_red);
                        break;
                    case R.id.right:
                        mGameView.mMario.mIsMove = true;
                        mGameView.mMario.xDirectionFlag = true;
                        mGameView.mMario.mMoveIdx = mGameView.mMario.mMoveFirst = 0;
                        mGameView.mMario.mMoveSize = 2;
                        mRightBtn.setBackgroundResource(R.drawable.key_right_red);
                        break;
                    case R.id.jump:
                        if (!mGameView.mMario.mIsJump && mGameView.mMario.mAlive && !mGameView.mMario.Isdo) {
                            mGameView.mMario.mIsStop = false;
                            mGameView.mMario.mIsJump = true;
                            mJumpBtn.setBackgroundResource(R.drawable.key_jump_red);
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.left:
                        mLeftBtn.setBackgroundResource(R.drawable.key_left_blue);
                        mGameView.mMario.mIsMove = false;
                        break;
                    case R.id.right:
                        mRightBtn.setBackgroundResource(R.drawable.key_right_blue);
                        mGameView.mMario.mIsMove = false;
                        break;
                    case R.id.jump:
                        mJumpBtn.setBackgroundResource(R.drawable.key_jump_blue);
                        break;
                }
                break;
        }
        return true;
    }
}
