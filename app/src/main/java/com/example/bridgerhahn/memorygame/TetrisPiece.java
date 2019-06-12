package com.example.bridgerhahn.memorygame;

import java.sql.Time;

public class TetrisPiece {
    private final int centerX;
    private final int centerY;
    private final long timeMade;
    public enum Shape{
        LShape,
        LShapeReverse,
        TShape,
        LineShape,
        SShape,
        SShapeReverse,
        BlockShape
    }
    private final Shape shape;

    private final int numRotated;

    public TetrisPiece(int centerX, int centerY, Shape shape, int numRotated) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.shape = shape;
        this.numRotated = numRotated;
        timeMade = System.currentTimeMillis();
    }

    public TetrisPiece() {
        centerX = 4;
        centerY = 0;
        int shapeNum = (int) (Math.random()*7);
        switch(shapeNum) {
            case 0:
                shape = Shape.LShape;
                break;
            case 1:
                shape = Shape.LShapeReverse;
                break;
            case 2:
                shape = Shape.TShape;
                break;
            case 3:
                shape = Shape.LineShape;
                break;
            case 4:
                shape = Shape.SShape;
                break;
            case 5:
                shape = Shape.SShapeReverse;
                break;
            case 6:
                shape = Shape.BlockShape;
                break;
            default:
                shape = Shape.BlockShape;
                break;
        }
        numRotated = 0;
        timeMade = System.currentTimeMillis();
    }


    public double secondsAlive() {
        return (System.currentTimeMillis() - timeMade) / 1000;
    }

    // Return new moved piece
    public TetrisPiece moveDown() {
        return new TetrisPiece(centerX, centerY + 1, shape, numRotated);
    }

    // Return new moved piece
    public TetrisPiece moveLeft() {
        return new TetrisPiece(centerX - 1, centerY, shape, numRotated);
    }

    // Return new moved piece
    public TetrisPiece moveRight() {
        return new TetrisPiece(centerX + 1, centerY, shape, numRotated);
    }

    // Return new rotated piece
    public TetrisPiece rotate() {
        return new TetrisPiece(centerX, centerY, shape, (numRotated+1) % 4);
    }

    public int[] getPositions() {
        int[] xvals = new int[4];
        int[] yvals = new int[4];
        int[] ret = new int[8];

        switch(shape) {
            case LShape:
                xvals[0] = 0;
                xvals[1] = 0;
                xvals[2] = 0;
                xvals[3] = 1;

                yvals[0] = -1;
                yvals[1] = 0;
                yvals[2] = 1;
                yvals[3] = 1;
                break;
            case LShapeReverse:
                xvals[0] = 0;
                xvals[1] = 0;
                xvals[2] = 0;
                xvals[3] = -1;

                yvals[0] = -1;
                yvals[1] = 0;
                yvals[2] = 1;
                yvals[3] = 1;
                break;
            case TShape:
                xvals[0] = -1;
                xvals[1] = 0;
                xvals[2] = 0;
                xvals[3] = 1;

                yvals[0] = 0;
                yvals[1] = 0;
                yvals[2] = 1;
                yvals[3] = 0;
                break;
            case LineShape:
                xvals[0] = 0;
                xvals[1] = 0;
                xvals[2] = 0;
                xvals[3] = 0;

                yvals[0] = -1;
                yvals[1] = 0;
                yvals[2] = 1;
                yvals[3] = 2;
                break;
            case SShape:
                xvals[0] = 0;
                xvals[1] = 0;
                xvals[2] = 1;
                xvals[3] = 1;

                yvals[0] = -1;
                yvals[1] = 0;
                yvals[2] = 0;
                yvals[3] = 1;
                break;
            case SShapeReverse:
                xvals[0] = 0;
                xvals[1] = 0;
                xvals[2] = -1;
                xvals[3] = -1;

                yvals[0] = -1;
                yvals[1] = 0;
                yvals[2] = 0;
                yvals[3] = 1;
                break;
            case BlockShape:
                ret[0] = 0 + centerX;
                ret[1] = 0 + centerY;
                ret[2] = 0 + centerX;
                ret[3] = 1 + centerY;
                ret[4] = 1 + centerX;
                ret[5] = 0 + centerY;
                ret[6] = 1 + centerX;
                ret[7] = 1 + centerY;

                return ret;
        }

        // Rotate numRotated times clockwise
        for(int i = 0; i < numRotated; i++) {
            // Rotate each point
            for(int j = 0; j < xvals.length; j++) {
                int newx = -1 * yvals[j];
                yvals[j] = xvals[j];
                xvals[j] = newx;
            }

        }

        // Fill in ret
        for(int i = 0; i < xvals.length; i++) {
            ret[i * 2] = xvals[i] + centerX;
            ret[i * 2 + 1] = yvals[i] + centerY;
        }

        return ret;

    }
}
