package com.example.bridgerhahn.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button memoryGameButton = findViewById(R.id.memoryGameButton);
        memoryGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMemory();
            }
        });

        Button snakeGameButton = findViewById(R.id.snakeGameButton);
        snakeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSnake();
            }
        });

        Button tetrisGameButton = findViewById(R.id.tetrisGameButton);
        tetrisGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTetris();
            }
        });
    }

    private void selectMemory() {
        Intent intent = new Intent(this, DifficultyActivity.class);
        intent.putExtra("game", "memory");
        startActivity(intent);
    }

    private void selectSnake() {
        Intent intent = new Intent(this, SnakeGameActivity.class);
        startActivity(intent);
    }

    private void selectTetris() {
        Intent intent = new Intent(this, TetrisGameActivity.class);
        startActivity(intent);
    }
}
