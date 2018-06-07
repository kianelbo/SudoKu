package com.kian.pitproject.sudoku;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends ActionBarActivity {
    TimeHandler timeHandler;
    TextView progressView, timeView;
    Cell[][] cells;
    Button[] digits;
    int lastClickedX, lastClickedY;
    SudokuModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        //initializing the model and timer
        timeView = findViewById(R.id.timeView);
        timeHandler = new TimeHandler(timeView);
        int startStatus = getIntent().getExtras().getInt("difficulty");
        if (startStatus == -1) {
            SharedPreferences savePrefs = getSharedPreferences("SAVE_PREFS", MODE_PRIVATE);
            model = new Gson().fromJson(savePrefs.getString("SavedModel", ""), SudokuModel.class);
            timeHandler.setSavedMillis(savePrefs.getLong("SavedTimer", 0));
        }
        else
            model = new SudokuModel(startStatus);


        //puzzle cells
        cells = new Cell[9][9];
        TableLayout table = findViewById(R.id.tableLayout);

        TableLayout.LayoutParams rowLp = new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f);
        TableRow.LayoutParams cellLp = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f);

        for (int i = 0; i < 9; i++) {
            TableRow row = new TableRow(this);
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new Cell(this, i, j);
                row.addView(cells[i][j], cellLp);
            }
            table.addView(row, rowLp);
        }


        //input digit buttons
        digits = new Button[9];
        for (int i = 0; i < 9; i++) {
            digits[i] = findViewById(R.id.button1 + i);
            final String buttonText = String.valueOf(i + 1);
            final int finalI = i;
            digits[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cells[lastClickedX][lastClickedY].setText(buttonText);
                    model.update(lastClickedX, lastClickedY, (char) (finalI + 49));
                    updateProgress();
                }
            });
        }
        Button clearButton = findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cells[lastClickedX][lastClickedY].setText("");
                model.update(lastClickedX, lastClickedY, '0');
                updateProgress();
            }
        });
        clearButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("Confirm reset?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < 9; i++)
                            for (int j = 0; j < 9; j++)
                                if (cells[i][j].hasOnClickListeners()) {
                                    cells[i][j].setText("");
                                    model.update(i, j, '0');
                                }
                        updateProgress();
                    }
                });
                dialog.setNegativeButton("Cancel", null);
                dialog.show();
                return false;
            }
        });


        //predefined cells
        String[] cellValues = model.getStartingModel();
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (cellValues[i].charAt(j) != '0') {
                    cells[i][j].setText(String.valueOf(cellValues[i].charAt(j)));
                    cells[i][j].setOnClickListener(null);
                    cells[i][j].setTypeface(null, Typeface.BOLD);
                    cells[i][j].setTextColor(Color.BLUE);
                }

        //first cell to be selected on start
        outer : for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (cellValues[i].charAt(j) == '0') {
                    cells[i][j].performClick();
                    break outer;
                }


        //loading saved game cell values
        if (startStatus == -1) {
            cellValues = model.getStatusModel();
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    if (cells[i][j].hasOnClickListeners())
                        cells[i][j].setText(String.valueOf(cellValues[i].charAt(j) != '0' ? cellValues[i].charAt(j) : ""));
        }

        //initial progress
        progressView = findViewById(R.id.progressView);
        updateProgress();
    }

    private void updateProgress() {
        int progress = model.getProgress();
        progressView.setText(String.format("%d/%d", progress, 81));

        //check win
        if (progress == 81)
            if (model.hasWon()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Congratulations!\nCompleted on " + timeView.getText())
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences.Editor prefsEditor = getSharedPreferences("SAVE_PREFS", MODE_PRIVATE).edit();
                                prefsEditor.putBoolean("SaveExists", false);
                                prefsEditor.apply();
                                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
        startActivity(intent);

        SharedPreferences.Editor prefsEditor = getSharedPreferences("SAVE_PREFS", MODE_PRIVATE).edit();
        prefsEditor.putString("SavedModel", new Gson().toJson(model));
        prefsEditor.putLong("SavedTimer", timeHandler.getElapsedTime());
        prefsEditor.putBoolean("SaveExists", true);
        prefsEditor.apply();

        finish();
    }

    private class Cell extends android.support.v7.widget.AppCompatTextView {
        private final ViewGroup.LayoutParams LAYOUT_PARAMS = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        public Cell(Context context, final int x, final int y) {
            super(context);

            setLayoutParams(LAYOUT_PARAMS);
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
            setGravity(TEXT_ALIGNMENT_CENTER);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            setBackgroundResource(((x / 3 + 1) * (y / 3 + 1) % 2) == 1 || (x / 3 + 1) * (y / 3 + 1) == 4 ?
                    R.drawable.cell_border2 : R.drawable.cell_border1);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    cells[lastClickedX][lastClickedY].setBackgroundResource(
                            ((lastClickedX / 3 + 1) * (lastClickedY / 3 + 1) % 2) == 1 || (lastClickedX / 3 + 1) * (lastClickedY / 3 + 1) == 4 ?
                                    R.drawable.cell_border2 : R.drawable.cell_border1);
                    setBackgroundColor(Color.YELLOW);
                    lastClickedX = x;
                    lastClickedY = y;
                }
            });
        }
    }
}
