package com.example.bridgerhahn.memorygame;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.MotionEvent;

public class TetrisGameBoard extends GameBoard implements Runnable {

    private double cellSize;
    private static final int NUM_CELLS_WIDE = 10;
    private static final int NUM_CELLS_HIGH = 20;

    private int startGridX;
    private int startGridY;
    private int bottomQuarter;

    private int upCX, upCY, downCX, downCY, leftCX, leftCY, rightCX, rightCY;
    private int buttonRad;

    boolean[][] board;
    TetrisPiece currentPiece;
    boolean isFastDropping;

    public TetrisGameBoard(Context context, Point screenSize) {
        super(context, screenSize);

        bottomQuarter = screenY * 3/4;

        cellSize = Math.min(screenX / NUM_CELLS_WIDE, bottomQuarter / NUM_CELLS_HIGH);
        startGridX = (int) (screenX - NUM_CELLS_WIDE * cellSize) / 2;
        startGridY = (int) (bottomQuarter - NUM_CELLS_HIGH * cellSize) / 2;

        upCX = screenX * 3/4;
        upCY = screenY * 13/16;
        downCX = upCX;
        downCY = screenY * 15/16;
        leftCX = screenX * 5/8;
        leftCY = screenY * 7/8;
        rightCX = screenX * 7/8;
        rightCY = leftCY;
        buttonRad = screenX / 16;
        newGame();
    }

    private void newGame() {
        //reset board
        board = new boolean[NUM_CELLS_WIDE][NUM_CELLS_HIGH];
        currentPiece = new TetrisPiece();
        isFastDropping = false;
        FPS = 5;
    }

    private boolean checkCollision(TetrisPiece piece) {
        int[] positions = piece.getPositions();
        for(int i = 0; i < 4; i++) {
            int x = positions[2*i];
            int y = positions[2*i+1];
            if(y < 0) continue;
            if(x < 0 || x >= NUM_CELLS_WIDE || y >= NUM_CELLS_HIGH || board[x][y]) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void update() {
        TetrisPiece nextPiece = currentPiece.moveDown();
        if(checkCollision(nextPiece)) {
            if (currentPiece.secondsAlive() < 0.5) return;
            lockPiece();
            return;
        }
        currentPiece = nextPiece;
        if(isFastDropping) {
            nextPiece = currentPiece.moveDown();
            if(checkCollision(nextPiece)) {
                lockPiece();
                return;
            }
            currentPiece = nextPiece;
        }

    }

    private void lockPiece() {

        int[] positions = currentPiece.getPositions();
        // Fill in spots on board for current piece
        for(int i = 0; i < 4; i++) {
            int x = positions[2*i];
            int y = positions[2*i+1];
            if(x < 0 || y < 0 || x >= NUM_CELLS_WIDE || y >= NUM_CELLS_HIGH) {
                newGame();
                return;
            }
            board[x][y] = true;
        }
        checkLines();
        currentPiece = new TetrisPiece();
    }

    private void checkLines() {
        for(int y = NUM_CELLS_HIGH - 1; y >= 0; y--) {
            boolean rowFull = true;
            for(int x = 0; x < NUM_CELLS_WIDE; x++) {
                if(!board[x][y]) {
                    rowFull = false;
                    break;
                }
            }
            if(rowFull) {
                clearRow(y);
                y++;
            }
        }
    }

    private void clearRow(int rowNum) {
        //clear the row
        for(int x = 0; x < NUM_CELLS_WIDE; x++) {
            board[x][rowNum] = false;
        }
        //bring every row down one
        for(int y = rowNum; y > 0; y--) {
            for(int x = 0; x < NUM_CELLS_WIDE; x++) {
                board[x][y] = board[x][y - 1];
            }
        }
        //top row is empty
        for(int x = 0; x< NUM_CELLS_WIDE; x++) {
            board[x][0] = false;
        }
    }

    @Override
    protected void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            //Fill the screen with blue background
            canvas.drawColor(Color.argb(255,0,0,0));
            paint.setARGB(255,26,128,182);
            canvas.drawRect(startGridX,startGridY, (float) (startGridX + NUM_CELLS_WIDE * cellSize), (float) (startGridY + NUM_CELLS_HIGH * cellSize), paint);

            //draw the pieces on the board
            paint.setARGB(255,255,255,255);
            for(int x = 0; x < NUM_CELLS_WIDE; x++) {
                for(int y = 0; y < NUM_CELLS_HIGH; y++) {
                    if(board[x][y]) {
                        int left = (int) (startGridX + x * cellSize);
                        int right = (int) (startGridX + (x + 1) * cellSize);
                        int top = (int) (startGridY + y * cellSize);
                        int bottom = (int) (startGridY + (y + 1) * cellSize);
                        canvas.drawRect(left, top, right, bottom, paint);
                    }
                }
            }

            // draw the current piece
            paint.setARGB(255,0,0,0);
            int[] positions = currentPiece.getPositions();
            for(int i = 0; i < 4; i++) {
                int x = positions[2 * i];
                int y = positions[2 * i + 1];
                if(x < 0 || x > NUM_CELLS_WIDE || y < 0 || y > NUM_CELLS_HIGH) continue;
                int left = (int) (startGridX + x * cellSize);
                int right = (int) (startGridX + (x + 1) * cellSize);
                int top = (int) (startGridY + y * cellSize);
                int bottom = (int) (startGridY + (y + 1) * cellSize);
                canvas.drawRect(left, top, right, bottom, paint);
            }

            //draw controls
            paint.setARGB(255,255,255,255);
            canvas.drawCircle(upCX, upCY, buttonRad, paint);
            canvas.drawCircle(downCX, downCY, buttonRad, paint);
            canvas.drawCircle(leftCX, leftCY, buttonRad, paint);
            canvas.drawCircle(rightCX, rightCY, buttonRad, paint);

            //draw triangels on controls
            paint.setARGB(128, 128, 128, 128);
            paint.setStyle(Paint.Style.FILL);
            Path path = new Path();
            path.moveTo(upCX, upCY - buttonRad / 2);
            path.lineTo(upCX - buttonRad / 2, upCY);
            path.lineTo(upCX + buttonRad / 2, upCY);
            path.lineTo(upCX, upCY - buttonRad / 2);
            path.close();
            canvas.drawPath(path, paint);
            path  = new Path();
            path.moveTo(downCX, downCY + buttonRad / 2);
            path.lineTo(downCX - buttonRad / 2, downCY);
            path.lineTo(downCX + buttonRad / 2, downCY);
            path.lineTo(downCX, downCY + buttonRad / 2);
            path.close();
            canvas.drawPath(path, paint);
            path = new Path();
            path.moveTo(leftCX - buttonRad / 2, leftCY);
            path.lineTo(leftCX, leftCY + buttonRad / 2);
            path.lineTo(leftCX, leftCY - buttonRad / 2);
            path.lineTo(leftCX - buttonRad / 2, leftCY);
            path.close();
            canvas.drawPath(path, paint);
            path = new Path();
            path.moveTo(rightCX + buttonRad / 2, rightCY);
            path.lineTo(rightCX, rightCY + buttonRad / 2);
            path.lineTo(rightCX, rightCY - buttonRad / 2);
            path.lineTo(rightCX + buttonRad / 2, rightCY);
            path.close();
            canvas.drawPath(path, paint);





            // Unlock the canvas and show render
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void tryLeft() {
        TetrisPiece newPiece = currentPiece.moveLeft();
        if(!checkCollision(newPiece)) currentPiece = newPiece;
    }

    private void tryRight() {
        TetrisPiece newPiece = currentPiece.moveRight();
        if(!checkCollision(newPiece)) currentPiece = newPiece;
    }

    private void tryRotate() {
        TetrisPiece newPiece = currentPiece.rotate();
        if(!checkCollision(newPiece)) currentPiece = newPiece;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            //check for distance from buttons
            if(Math.sqrt(Math.pow(x-upCX, 2) + Math.pow(y - upCY, 2)) < buttonRad) {
                tryRotate();
            } else if(Math.sqrt(Math.pow(x-downCX, 2) + Math.pow(y - downCY, 2)) < buttonRad) {
                isFastDropping = true;
            } else if(Math.sqrt(Math.pow(x-leftCX, 2) + Math.pow(y - leftCY, 2)) < buttonRad) {
                tryLeft();
            } else if(Math.sqrt(Math.pow(x-rightCX, 2) + Math.pow(y - rightCY, 2)) < buttonRad) {
                tryRight();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if(isFastDropping) isFastDropping = false;
        }
        return true;
    }
}
