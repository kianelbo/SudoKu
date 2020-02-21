package com.kian.sudoku

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu.*
import com.kian.sudoku.SudokuModel.Companion.EASY_CONST
import com.kian.sudoku.SudokuModel.Companion.HARD_CONST
import com.kian.sudoku.SudokuModel.Companion.MEDIUM_CONST


class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // new game button
        newGameButton.setOnClickListener {
            val difficulty: Int = when (radioDifficulty.checkedRadioButtonId) {
                easyRadio.id -> EASY_CONST
                hardRadio.id -> HARD_CONST
                else -> MEDIUM_CONST
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("difficulty", difficulty)
            startActivity(intent)
        }

        // resume button
        if (getSharedPreferences("SAVE_PREFS", Context.MODE_PRIVATE).getBoolean("Saved", false))
            resumeButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("difficulty", -1)
                startActivity(intent)
            }
        else resumeButton.isEnabled = false

        // stats button
        statsButton.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }

        // about button
        aboutButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            dialogBuilder.setTitle("Android Sudoku")
            dialogBuilder.setMessage("Created by kianelbo - version $version")
            dialogBuilder.create().show()
        }

        // exit button
        exitButton.setOnClickListener {
            finishAndRemoveTask()
        }
    }
}
