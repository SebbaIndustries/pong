package com.sebbaindustries.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * <b>This class is used creating abstract for game, lowkey a game engine</b>
 * @author sebbaindustries
 * @version 1.0
 */
public abstract class GameView extends SurfaceView implements SurfaceHolder.Callback {

    static final String LOG_TAG = GameView.class.getSimpleName();

    private static final float MAX_FPS = 60;
    private static final float MIN_DT = 1000.0f / MAX_FPS;

    private Thread drawingThread;
    private final SurfaceHolder surfaceHolder;
    private FpsCounter fpsCounter;

    /** time since the start of the game in milliseconds */
    private long time;

    private float timeDelta;

    /**
     * Main constructor, prepares game and acces to the engine methods
     * @param context App
     */
    public GameView(Context context) {
        super(context);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    /**
     * Fps thing, updates fps and timer, also call method onUpdate for
     * actual game to use
     */
    private void update() {
        long now = System.currentTimeMillis();
        timeDelta = 0.001f * (time > 0 ? now - time : 0);
        if (timeDelta != 0 && fpsCounter != null) {
            fpsCounter.updateFps(1.0 / timeDelta);
        }
        time = now;

        onUpdate();
    }

    protected abstract void onUpdate();

    protected abstract void onRender(Canvas canvas);

    /**
     * Detects if surface is changed
     * @param holder game holder
     * @param format game format
     * @param width game width
     * @param height game height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * creates game instance
     * @param holder surface holder for game
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO maybe add async methods?
        drawingThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    update();
                    Canvas canvas = null;
                    // this actually worked first try :D
                    try {
                        canvas = surfaceHolder.lockCanvas();
                        if (canvas != null) {
                            onRender(canvas);
                        }
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }

                    long now = System.currentTimeMillis();
                    float sleepTime = (time - now) + MIN_DT;
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep((long) sleepTime);
                        } catch (InterruptedException e) {
                            Log.i(LOG_TAG, "interrupted");
                        }
                    }
                }
            }
        });
        drawingThread.start();
    }

    /**
     * destroys game instance
     * @param holder surface holder for game
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != drawingThread) {
            // this is actually not that smart to do, but it's for a school project so...
            drawingThread.interrupt();
        }
    }

    public void pause() {
        // TODO better resolve stopping the game thread, I probably never will
        surfaceDestroyed(getHolder());
    }

    /**
     * creates fps counter instance
     * @param fpsCounter class
     */
    public void setFpsCounter(FpsCounter fpsCounter) {
        this.fpsCounter = fpsCounter;
    }

    /**
     * TODO switch to double
     * gets game time
     * @return float time
     */
    protected float getTimeDelta() {
        return timeDelta;
    }

}
