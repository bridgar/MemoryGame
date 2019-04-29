package com.example.bridgerhahn.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DifficultyActivity extends AppCompatActivity {

    private String game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        game = intent.getStringExtra("game");
        setContentView(R.layout.activity_difficulty);

        Button easy = findViewById(R.id.easyButton);
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(game, 2);
            }
        });

        Button medium = findViewById(R.id.mediumButton);
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(game, 3);
            }
        });

        Button hard = findViewById(R.id.hardButton);
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(game, 4);
            }
        });
    }

    private void startGame(String gameType, int difficulty) {
        if(gameType.equals("memory")) {
            Intent intent = new Intent(this, MemoryGameActivity.class);
            intent.putExtra("difficulty", difficulty);
            startActivity(intent);
        }
    }
}
