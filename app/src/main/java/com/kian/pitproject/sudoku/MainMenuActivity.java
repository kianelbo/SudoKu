package com.kian.pitproject.sudoku;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        //exit button
        findViewById(R.id.exitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        //about button
        findViewById(R.id.aboutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainMenuActivity.this);
                dialog.setTitle("SudoKu");
                dialog.setMessage("Android sudoku puzzle\nCreated by Kian Eliasi\nVersion 1.0 beta");
                dialog.show();
            }
        });


        //difficulty and new game
        final RadioGroup radioGroup = findViewById(R.id.difficultyRadioGroup);
        findViewById(R.id.newGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                int difficulty;
                if (selectedId == R.id.easyRadio) difficulty = 30;
                else if (selectedId == R.id.mediumRadio) difficulty = 44;
                else difficulty = 58;

                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putInt("difficulty", difficulty);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });


        //continue button
        Button continueButton = findViewById(R.id.continueButton);
        if (getSharedPreferences("SAVE_PREFS", MODE_PRIVATE).getBoolean("SaveExists", false))
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("difficulty", -1);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });
        else
            continueButton.setEnabled(false);
    }
}
