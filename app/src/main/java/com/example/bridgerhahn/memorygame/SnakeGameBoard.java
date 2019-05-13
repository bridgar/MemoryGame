package com.example.bridgerhahn.memorygame;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.LinkedList;

public class SnakeGameBoard extends GameBoard implements Runnable {

    private double cellSize;
    private static final int NUM_CELLS_HIGH = 30;
    private int numCellsWide;

    private int foodX;
    private int foodY;

    private enum Direction{
        UP, DOWN, LEFT, RIGHT
    }
    private Direction direction;
    private LinkedList<int[]> segments;

    private float leftQuarter;
    private float rightQuarter;
    private float topQuarter;
    private float bottomQuarter;


    SnakeGameBoard(Context context, Point screenSize) {
        super(context, screenSize, 5);
        cellSize = screenY / NUM_CELLS_HIGH;
        numCellsWide = (int) (screenX / cellSize);

        leftQuarter = screenX / 4;
        rightQuarter = screenX * 3 / 4;
        topQuarter = screenY / 4;
        bottomQuarter = screenY * 3 / 4;


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

            //draw control lines
            paint.setARGB(255, 0, 0, 0);
            canvas.drawLine(0,0, screenX, screenY, paint);
            canvas.drawLine(0, screenY, screenX, 0, paint);

            //draw control boxes
//            paint.setARGB(100, 100, 100, 100);
//            canvas.drawRect(0, 0, leftQuarter, screenY, paint);
//            canvas.drawRect(rightQuarter, 0, screenX, screenY, paint);
//            canvas.drawRect(leftQuarter, 0, rightQuarter, topQuarter, paint);
//            canvas.drawRect(leftQuarter, bottomQuarter, rightQuarter, screenY, paint);

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
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                boolean belowLine1 = y > x * screenY / screenX; // Line 1 goes from topLeft to bottomRight
                boolean belowLine2 = y > screenY - x * screenY / screenX; // Line 2 goes from bottomLeft ot topRight

                if(belowLine1) {
                    if(belowLine2) {
                        direction = Direction.DOWN; // below both lines
                    } else {
                        direction = Direction.LEFT; // below only line 1
                    }
                } else {
                    if(belowLine2) {
                        direction = Direction.RIGHT; // below only line 2
                    } else {
                        direction = Direction.UP; // below neither line
                    }
                }

//                if(x < leftQuarter) {
//                    direction = Direction.LEFT;
//                } else if(x > rightQuarter) {
//                    direction = Direction.RIGHT;
//                } else if(y < topQuarter) {
//                    direction = Direction.UP;
//                } else if(y > bottomQuarter) {
//                    direction = Direction.DOWN;
//                }
        }
        return true;
    }
}
