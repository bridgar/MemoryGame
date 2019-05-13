package com.example.bridgerhahn.memorygame;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class SnakeGameActivity extends AppCompatActivity {

    private SnakeGameBoard board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        board = new SnakeGameBoard(this, size);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen

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
