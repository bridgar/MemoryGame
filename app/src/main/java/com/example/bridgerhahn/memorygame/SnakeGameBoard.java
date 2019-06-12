package com.example.bridgerhahn.memorygame;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import java.util.LinkedList;

public class SnakeGameBoard extends GameBoard implements Runnable {

    private double cellSize;
    private static final int NUM_CELLS_HIGH = 30;
    private int numCellsWide;

    private int foodX;
    private int foodY;

    public enum Direction{
        UP, DOWN, LEFT, RIGHT
    }
    private Direction direction;
    private LinkedList<int[]> segments;

    private static final int MIN_SWIPE_DISTANCE_X = 100;
    private static final int MIN_SWIPE_DISTANCE_Y = 100;

    private static final int MAX_SWIPE_DISTANCE_X = 100;
    private static final int MAX_SWIPE_DISTANCE_Y = 100;

    private VelocityTracker mVelocityTracker = null;
    private boolean sameTouch = false;


    SnakeGameBoard(Context context, Point screenSize) {
        super(context, screenSize, 5);
        cellSize = screenY / NUM_CELLS_HIGH;
        numCellsWide = (int) (screenX / cellSize);

        newGame();
    }

    @Override
    protected void update() {
        // Look at the next space
        int[] head = segments.getFirst();
        int[] next = new int[2];
        next[0] = head[0];
        next[1] = head[1];
        switch(direction){
            case UP:
                next[1]--;
                break;
            case DOWN:
                next[1]++;
                break;
            case LEFT:
                next[0]--;
                break;
            case RIGHT:
                next[0]++;
                break;
        }

        // check for loss conditions
        // check for running into wall
        if (next[0] < 0 || next[0] >= numCellsWide || next[1] < 0 || next[1] >= NUM_CELLS_HIGH) {
            newGame();
            return;
        }
        // check for running into self
        for(int[] segment : segments) {
            if(next[0] == segment[0] && next[1] == segment[1]) {
                newGame();
                return;
            }
        }

        // move forward
        segments.addFirst(next);

        //check for scoring point
        if(next[0] == foodX && next[1] == foodY) {
            score++;
            newFood();

        } else { // Didn't eat food and doesn't get longer
            segments.removeLast();
        }
    }

    @Override
    protected void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            //Fill the screen with blue background
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            //draw fruit
            paint.setARGB(255, 166, 31, 52);
            int left = (int) (foodX * cellSize);
            int right = (int) ((foodX + 1) * cellSize);
            int top = (int) (foodY * cellSize);
            int bottom = (int) ((foodY + 1) * cellSize);
            canvas.drawRect(left, top, right, bottom, paint);

            //draw snake segments
            paint.setARGB(255, 255, 255, 255);
            for(int[] segment : segments) {
                left = (int) (segment[0] * cellSize);
                right = (int) ((segment[0] + 1) * cellSize);
                top = (int) (segment[1] * cellSize);
                bottom = (int) ((segment[1] + 1) * cellSize);
                canvas.drawRect(left, top, right, bottom, paint);
            }

            // Unlock the canvas and show render
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void newGame() {
        direction = Direction.UP;

        segments = new LinkedList<int[]>();
        int[] middle = {numCellsWide/2, NUM_CELLS_HIGH / 2};
        segments.push(middle);

        score = 1;
        newFood();
    }

    private void newFood() {
        boolean isInSnake = false;
        do {
            isInSnake = false;
            foodX = (int) (Math.random() * numCellsWide);
            foodY = (int) (Math.random() * NUM_CELLS_HIGH);
            for(int[] pair : segments) {
                if(pair[0] == foodX && pair[1] == foodY) {
                    isInSnake = true;
                    break;
                }
            }

        } while (isInSnake);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if(mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                sameTouch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xv = mVelocityTracker.getXVelocity(pointerId);
                float yv = mVelocityTracker.getYVelocity(pointerId);
                if(!sameTouch) {
                    if (Math.abs(xv) > Math.abs(yv)) {
                        if (xv > 0) {
                            direction = Direction.RIGHT;
                        } else {
                            direction = Direction.LEFT;
                        }
                    } else {
                        if (yv > 0) {
                            direction = Direction.DOWN;
                        } else {
                            direction = Direction.UP;
                        }
                    }
                    sameTouch = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                sameTouch = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
        }
        return true;

    }


}
