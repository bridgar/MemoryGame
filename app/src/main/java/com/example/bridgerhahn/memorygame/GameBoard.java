package com.example.bridgerhahn.memorygame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class GameBoard extends SurfaceView implements Runnable {

    // Game thread for running main loop
    private Thread thread = null;

    // Reference back to Activity
    private Context context;

    // Screen Size
    protected int screenX;
    protected int screenY;

    // Control frame rate
    protected long nextFrameTime;
    protected long flipBackTime;
    protected boolean needsFlipBack = false;
    protected final long SECONDS_BEFORE_FLIP_BACK = 1;
    protected final long FPS;
    protected final long MILLIS_PER_SECOND = 1000;

    // Is the game currently running?
    private volatile boolean isPlaying;

    // Player score
    protected int score;


    // Things needed to draw
    protected Canvas canvas;
    protected SurfaceHolder surfaceHolder;
    protected Paint paint;

    GameBoard(Context context, Point screenSize) {
        super(context);
        FPS = 10; //default fps

        this.context = context;
        screenX = screenSize.x;
        screenY = screenSize.y;


        // Initialize drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();
    }

    GameBoard(Context context, Point screenSize, long fps) {
        super(context);
        FPS = fps;

        this.context = context;
        screenX = screenSize.x;
        screenY = screenSize.y;


        // Initialize drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();
    }

    protected abstract void update();
    protected abstract void draw();

    @Override
    public void run() {
        while(isPlaying) {
            // Update at desired FPS
            if(updateRequired()) {
                update();
                draw();
            }
        }
    }

    void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            //TODO real error handling
            System.out.println("Pause Failed?");
        }
    }

    void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }


    private boolean updateRequired() {
        // Do we need an update
        if(nextFrameTime <= System.currentTimeMillis()) {
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            return true;
        }
        return false;
    }
}
