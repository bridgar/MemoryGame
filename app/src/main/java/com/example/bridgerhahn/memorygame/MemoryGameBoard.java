package com.example.bridgerhahn.memorygame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.graphics.ColorUtils;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MemoryGameBoard extends GameBoard implements Runnable{

    private int[] colors;

    // Vertical margin in percent of screen
    private static final double VERTICAL_MARGIN_PERCENT = .05;

    // Number of rows/columns to game board
    private int numRows;
    private int numColumns;

    // Size of cards in pixels
    private int cardSize;
    // X Y coords of the top left of the first card
    private int firstX;
    private int firstY;

    // Player score
    private int matched;
    private int score;
    private static final int POINTS_PER_MATCH = 1000;
    private static final int SCORE_DECREASE_PER_SECOND = 10;

    // Number of times the board will be shuffled before play
    private static final int NUM_RANDOMIZES = 5;
    // Array of cards in their positions on the board
    private MemoryCard[][] cards;

    private boolean isFirstSelected = false;
    private MemoryCard firstSelected;
    private MemoryCard secondSelected;

    /**
     *
     * @param difficulty corresponds to the number of rows/columns to the game
     */
    MemoryGameBoard(Context context, int difficulty, Point screenSize) {
        super(context, screenSize);

        numRows = difficulty * 2;
        numColumns = difficulty *2;

        // Find how much of the screen can be used by the cards
        int useableY = (int) (screenY * (1.0 - VERTICAL_MARGIN_PERCENT * 2));
        cardSize = useableY / numRows;
        firstY = (int) (screenY * VERTICAL_MARGIN_PERCENT);
        firstX = (screenX - cardSize * numColumns) / 2;

        // Start the game
        newGame();
    }

    private void newGame() {
        cards = new MemoryCard[numRows][numColumns];

        // Number of unique cards needed
        int numIdentities = numRows * numColumns / 2;
        // Create the ordering of cards
        int[] order = new int[numIdentities * 2];
        // Populate ordering of cards
        for(int i = 0; i < numIdentities; i++) {
            order[2*i] = i;
            order[2*i + 1] = i;
        }
        // Randomize order of cards
        for(int j = 0; j < NUM_RANDOMIZES; j++) {
            for (int i = 0; i < order.length; i++) {
                int r = (int) (Math.random() * order.length);
                int temp = order[i];
                order[i] = order[r];
                order[r] = temp;
            }
        }

        // Populate board array
        for(int i = 0; i < order.length; i++) {
            int row = i / numColumns;
            int column = i % numColumns;
            cards[row][column] = new MemoryCard(order[i]);
        }

        // Set up evenly spaced colors for cards
        colors = new int[numRows * numColumns / 2];
        for(int i = 0; i < colors.length; i++) {
            float[] hsl = {(float) (i * 360.0 / colors.length), 1, 0.5f};
            colors[i] = ColorUtils.HSLToColor(hsl);
        }

        // Reset score
        score = 0;
        matched = 0;

        // Set up for an update
        nextFrameTime = System.currentTimeMillis();
        flipBackTime = System.currentTimeMillis();
    }

    protected void update() {
        // Check for win
        if(matched >= numColumns * numRows / 2) {
            newGame();
            //TODO something nicer than just starting over
        }

        // Check if flip back is needed
        if(needsFlipBack && flipBackTime <= System.currentTimeMillis()) {
            firstSelected.flipCard();
            secondSelected.flipCard();
            needsFlipBack = false;
        }

        //Lose points every update
        score = Math.max(score - (int) (SCORE_DECREASE_PER_SECOND / FPS), 0);
    }

    protected void draw() {
        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            //Fill the screen with blue background
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            paint.setColor(Color.argb(255, 255, 255, 255));

            // Scale HUD text
            paint.setTextSize(90);
            canvas.drawText("Score:" + score, 10, 70, paint);

            // Draw each card
            for(int x = 0; x < numColumns; x++) {
                for(int y = 0; y < numRows; y++) {
                    int left = firstX + cardSize * x + 1;
                    int right = firstX + cardSize * (x+1) - 1;
                    int top = firstY + cardSize * y + 1;
                    int bottom = firstY + cardSize * (y+1) - 1;
                    if(cards[y][x].isFaceUp()) {
                        paint.setColor(colors[cards[y][x].getIdentity()]);
                    } else {
                        paint.setColor(Color.argb(255,255,255,255));
                    }
                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }

            // Unlock the canvas and show render
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                // If outside the play area, do nothing
                if(x < firstX || x > firstX + cardSize * numColumns) return true;
                if(y < firstY || y > firstY + cardSize * numRows) return true;

                int i = (int) ((x - firstX) / cardSize);
                int j = (int) ((y - firstY) / cardSize);
                selectCard(i, j);
        }
        return true;
    }

    /**
     *
     * @param x x coord in card array
     * @param y y coord in card array
     */
    private void selectCard(int x, int y) {
        //If it's too soon to flip again
        if(needsFlipBack) return;

        MemoryCard selected = cards[y][x];
        if(selected.isFaceUp()) {
            //TODO real message output
            System.out.println("This card is already face-up. Select a face-down card.");
            return;
        }

        selected.flipCard();
        //First selection
        if(!isFirstSelected) {
            isFirstSelected = true;
            firstSelected = selected;
        }
        //Second selection
        else {
            // Check for match
            if(firstSelected.getIdentity() == selected.getIdentity()) {
                score += POINTS_PER_MATCH;
                matched++;
            } else {
                secondSelected = selected;
                needsFlipBack = true;
                flipBackTime = System.currentTimeMillis() + SECONDS_BEFORE_FLIP_BACK * MILLIS_PER_SECOND;
            }
            isFirstSelected = false;
        }

    }
}
