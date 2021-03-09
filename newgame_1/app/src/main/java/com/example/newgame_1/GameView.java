package com.example.newgame_1;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mHolder;
    // 定义二级缓存bitmap
    private Bitmap mBmGameCache;
    // 定义资源图片
    public Bitmap mBmRoad_1, mBmRoad_2, mBmRoad_3,
            mBmGround_1/*未用到*/, mBmGround_2,
            mBullet_1, mBullet_2,
            mFlyMonster,
            mBmTortoise, mBmTortoiseDead;
    // 定义马里奥对象
    public SuperMario mMario;

    // 定义界面所有路对象
    public Road mGround_1, mGround_2,
            mRoad1, mRoad2, mRoad3, mRoad4;

    // 定义背景
    private BgMap GameBg;
    private float Alltime = 0;
    public int mScreenWidth, mScreenHeight, StartY, AllScore = 0;
    public int timeA = 0, limmitA = 200, timeS = 0, limmitS = 20;
    public int CountStar = 0;
    public boolean IsAddStar = true, stopstate = false;
    private Paint mPaint, mPaintScore;
    public Random random = new Random();
    private Thread thread1, thread2, thread3;
    private Typeface typeface;
    private boolean MainThreadFlag = true, IsAddMonsterA = true;

    public List<IGameObject> mRoads = new CopyOnWriteArrayList<>();
    public List<IGameObject> mHeroes = new CopyOnWriteArrayList<>();
    public List<IGameObject> stars = new CopyOnWriteArrayList<>();
    public List<IGameObject> monsters = new CopyOnWriteArrayList<>();
    public List<IGameObject> explodes = new CopyOnWriteArrayList<>();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);

        typeface = Typeface.createFromAsset(context.getAssets(), "pty.TTF");// 初始化字体
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mBmGameCache = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Config.ARGB_8888);// 初始化二级缓存bitmap
        GameBg = new BgMap(this);

        // 加载资源图片
        mBmGround_1 = BitmapFactory.decodeResource(getResources(), R.drawable.ground_1);
        mBmGround_2 = BitmapFactory.decodeResource(getResources(), R.drawable.ground_2);
        mBullet_1 = BitmapFactory.decodeResource(getResources(), R.drawable.bullet_1);
        mBmRoad_1 = BitmapFactory.decodeResource(getResources(), R.drawable.road_1);
        mBmRoad_2 = BitmapFactory.decodeResource(getResources(), R.drawable.road_2);
        mBmRoad_3 = BitmapFactory.decodeResource(getResources(), R.drawable.road_3);
        mFlyMonster = BitmapFactory.decodeResource(getResources(), R.drawable.monster_1);
        mBmTortoise = BitmapFactory.decodeResource(getResources(), R.drawable.all_tortoise);
        mBmTortoiseDead = BitmapFactory.decodeResource(getResources(), R.drawable.tortoise_dead);

        // 初始化画笔
        mPaintScore = new Paint();
        mPaint = new Paint();

        init();
        // 初始跳跳蘑菇并将它加入绘制容器
        FlyMonster fMonster = new FlyMonster(mFlyMonster, 850, 100, 1, this);
        monsters.add(fMonster);
    }

    public void init() {
        float den = getResources().getDisplayMetrics().density;

        // 设置画笔的类型属性等
        mPaintScore.setTypeface(typeface);
        mPaintScore.setColor(Color.rgb(180, 100, 220));
        mPaintScore.setShadowLayer(8, 3, 3, Color.CYAN);
        mPaintScore.setTextSkewX(-0.5f);
        mPaintScore.setTextSize(20 * den);

        // 初始化各个路对象
        // 左下方地面
        mGround_1 = new Road(mBmGround_2, 0, mScreenHeight - mBmGround_2.getHeight(), 0, this);
        // 右下方地面
        mGround_2 = new Road(mBmGround_2, mScreenWidth - mBmGround_2.getWidth(), mScreenHeight - mBmGround_2.getHeight(), 0, this);
        // 上下移动
        mRoad1 = new Road(mBmRoad_2, (int) (300 * den), (int) (120 * den), 1, this);
        // 左右移动
        mRoad2 = new Road(mBmRoad_2, (int) (150 * den), (int) (180 * den), 2, this);
        // 长板、低处、静止
        mRoad3 = new Road(mBmRoad_1, (int) (480 * den), (int) (200 * den), 0, this);
        // 短板、高处、静止
        mRoad4 = new Road(mBmRoad_3, (int) (580 * den), (int) (100 * den), 0, this);

        // 初始化特殊路-炮塔对象
        Bitmap bmTurret = BitmapFactory.decodeResource(getResources(), R.drawable.turret);
        Road turretRoad = new Road(bmTurret, (int) (210 * den), (int) (180 * den - bmTurret.getHeight()), 3, this);

        // 初始化马里奥对象
        mMario = new SuperMario((int) (60 * den), mScreenHeight * 3 / 4, this);

        // 将各个对象放入各绘制容器
        mRoads.add(mRoad1);
        mRoads.add(mRoad2);
        mRoads.add(mRoad3);
        mRoads.add(mRoad4);
        mRoads.add(mGround_1);
        mRoads.add(mGround_2);
        mRoads.add(turretRoad);
        mHeroes.add(mMario);
    }

    public void mainDraw() {
        Alltime += 0.03f;
        Canvas c = new Canvas(mBmGameCache);//获取缓存bitmap的画布
        c.drawBitmap(GameBg.getBitmap(), GameBg.getX(), GameBg.getY(), mPaint);
        for (IGameObject prop : stars) {
            c.drawBitmap(prop.getBitmap(), prop.getX(), prop.getY(), mPaint);
        }
        c.drawText("时间: " + String.format("%.2f", Alltime), 20, 60, mPaintScore);
        c.drawText("金币: ", mScreenWidth / 2 - 90, 60, mPaintScore);
        c.drawText("分数: " + AllScore, mScreenWidth - 300, 60, mPaintScore);
//    	c.drawText("IsJump: "+jumpTest.IsJump, ScreenWidth-300,80, scorepaint);
//    	c.drawText("size: "+jumpTest.now.size(), ScreenWidth-300,120, scorepaint);
        for (IGameObject prop : mRoads) {
            c.drawBitmap(prop.getBitmap(), prop.getX(), prop.getY(), mPaint);
        }

        for (IGameObject prop : mHeroes) {
            if (prop instanceof SuperMario) {
                SuperMario my = (SuperMario) prop;
                if (my.mAlive) {
                    my.IsImpact(mRoads);
                }
            }
            c.drawBitmap(prop.getBitmap(), prop.getX(), prop.getY(), mPaint);
        }

        for (IGameObject prop : explodes) {
            c.drawBitmap(prop.getBitmap(), prop.getX(), prop.getY(), mPaint);
        }

        for (IGameObject prop : monsters) {
            if (prop instanceof Monster) {
                Monster monster = (Monster) prop;
                if (mMario.mAlive) {
                    monster.IsImpact(mMario);
                }
                monster.IsImpactRoad(mRoads);
                monster.IsImpactTortoise(monsters);
            }
            if (prop instanceof BombA) {
                BombA bombA = (BombA) prop;
                if (mMario.mAlive) {
                    bombA.IsImpact(mMario);
                }
            }
            if (prop instanceof FlyMonster) {
                FlyMonster flayMonster = (FlyMonster) prop;
                if (mMario.mAlive) {
                    flayMonster.IsImpact(mMario);
                }
                flayMonster.IsImpactRoad(mRoads);
            }
            if (prop instanceof Tortoise) {
                Tortoise tortoise = (Tortoise) prop;
                if (mMario.mAlive) {
                    tortoise.IsImpact(mMario);
                }
                tortoise.IsImpactRoad(mRoads);
                tortoise.IsImpactTortoise(monsters);
            }
            c.drawBitmap(prop.getBitmap(), prop.getX(), prop.getY(), mPaint);
        }

        Canvas canvas = null;
        try {
            if (mHolder != null) {
                canvas = mHolder.lockCanvas();
                canvas.drawBitmap(mBmGameCache, 0, 0, mPaint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void stop() {
        stopstate = true;
    }

    public void start() {
        stopstate = false;
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
    }

    @Override
    public void run() {
        while (MainThreadFlag) {
            while (stopstate) {
                try {
                    Thread.sleep(100000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                mainDraw();
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        thread1 = new Thread(this);
        thread1.start();
        thread2 = new Thread(new DetectionTask());
        thread2.start();
        thread3 = new Thread(new AddSpriteTask(this));
        thread3.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        MainThreadFlag = false;
        this.mHolder.removeCallback(this);
    }

    class DetectionTask implements Runnable {

        @Override
        public void run() {
            while (MainThreadFlag) {
                while (stopstate) {
                    try {
                        Thread.sleep(100000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    exeDetection(mMario);
                    exeDetection2(monsters);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void exeDetection(IGameObject prop) {
            SuperMario my = null;
            List<IGameObject> list = null;
            Road road = null;
            if (prop instanceof SuperMario) {
                my = (SuperMario) prop;
                list = my.now;
            }
            if (list != null) {
                if (list.size() == 1) {
                    for (IGameObject gFace : list) {
                        if (gFace instanceof Road) {
                            road = (Road) gFace;
                        } else {
                            road = null;
                        }
                    }
                    if (road != null) {
                        if (isCollisionWithRect2(my.x, my.y, my.width, my.height, road.getX(), road.getY(), road.getWidth(), road.getHeight())) {
                            switch (road.mode) {
                                case 1:
                                    if (my.now.size() == 1) {
                                        my.now.remove(road);
                                    }
                                    break;
                            }
                            my.Isdo = true;
                        }
                    }
                }
            }
        }

        public boolean isCollisionWithRect(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
            return x1 + w1 >= x2 && y1 + h1 >= y2 && x1 <= x2 + w2 && y1 <= y2 + h2;
        }

        public void exeDetection2(List<IGameObject> props) {
            Monster monster = null;
            Tortoise tortoise = null;
            List<IGameObject> list = null;
            Road road = null;

            for (IGameObject prop : props) {
                if (prop instanceof Monster) {
                    monster = (Monster) prop;
                    list = monster.now;
                } else if (prop instanceof Tortoise) {
                    tortoise = (Tortoise) prop;
                    list = tortoise.now;
                }

                if (list != null) {
                    if (list.size() == 1) {
                        for (IGameObject gFace : list) {
                            if (gFace instanceof Road) {
                                road = (Road) gFace;
                            } else {
                                road = null;
                            }
                        }
                        if (road != null) {
                            if (monster != null) {
                                if (isCollisionWithRect2(monster.x, monster.y, monster.width, monster.height, road.getX(), road.getY(), road.getWidth(), road.getHeight())) {
                                    switch (road.mode) {
                                        case 1:
                                            if (monster.now.size() == 1) {
                                                monster.now.remove(road);
                                            }
                                            break;
                                    }
                                    monster.IsOnRoad = false;
                                }
                            }
                            if (tortoise != null) {
                                if (isCollisionWithRect2(tortoise.x, tortoise.y, tortoise.width, tortoise.height, road.getX(), road.getY(), road.getWidth(), road.getHeight())) {
                                    switch (road.mode) {
                                        case 1:
                                            if (tortoise.now.size() == 1) {
                                                tortoise.now.remove(road);
                                            }
                                            break;
                                    }
                                    tortoise.IsOnRoad = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        public boolean isCollisionWithRect2(int x1, int y1, int w1, int h1,
                                            int x2, int y2, int w2, int h2) {
            return y1 + h1 == y2 && x1 + w1 < x2 + 10 || x1 > x2 + w2 - 10;
        }
    }

    class AddSpriteTask implements Runnable {
        private GameView gameView;

        public AddSpriteTask(GameView gameView) {
            this.gameView = gameView;
        }

        @Override
        public void run() {
            while (MainThreadFlag) {
                while (stopstate) {
                    try {
                        Thread.sleep(100_000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    addSprite();
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void addSprite() {
            timeA++;
            if (timeA >= limmitA) {
                if (IsAddMonsterA) {
                    Tortoise tortoise = new Tortoise(mBmTortoise, mBmTortoiseDead, 600, 60, 1, 2, gameView);
                    monsters.add(tortoise);
                    Monster monster = null;
                    limmitA = random.nextInt(220) + 280;
                    //int varA=random.nextInt(30);
                    int varB = random.nextInt(40);
                    int x = 0, y = 0, mode = 0;
                    x = random.nextInt(mBmRoad_2.getWidth()) + 380;
                    y = 200 - random.nextInt(200);
                    if (varB <= 10) {
                        mode = 1;
                    } else if (varB > 10 && varB <= 20) {
                        mode = 2;
                    } else if (varB > 20 && varB <= 30) {
                        mode = 3;
                    } else {
                        mode = 4;
                    }
                    monster = new Monster(x, y, mode, gameView);
                    monsters.add(monster);
                    timeA = 0;
                }
            }
            if (IsAddStar) {
                timeS++;
                if (timeS >= limmitS) {
                    limmitS = random.nextInt(100) + 100;
                    int x = random.nextInt(mScreenWidth - 200) + 100;
                    Star star = new Star(x, -100, 1, gameView);
                    stars.add(star);
                    CountStar++;
                    if (CountStar >= 10) {
                        IsAddStar = false;
                    }
                    timeS = 0;
                }
            }
        }
    }
}
