package com.example.bridgerhahn.memorygame;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;

public class GameActivity extends AppCompatActivity {

    GameBoard board;

    private static final int GAME_DIFFICULTY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        board = new GameBoard(this, GAME_DIFFICULTY, size);

        setContentView(board);
    }

    // Start the thread in the game board
    @Override
    protected void onResume() {
        super.onResume();
        board.resume();
    }

    // Stop the thread in the game board
    @Override
    protected void onPause() {
        super.onPause();
        board.pause();
    }
}
