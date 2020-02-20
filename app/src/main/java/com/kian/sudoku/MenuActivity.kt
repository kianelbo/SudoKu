package com.kian.sudoku

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // new game button
        newGameButton.setOnClickListener {
            val difficulty: Int = when (radioDifficulty.checkedRadioButtonId) {
                easyRadio.id -> 32
                hardRadio.id -> 60
                else -> 45
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
    }

    // about button
    fun showAbout(view: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(R.string.app_name)
        dialogBuilder.setMessage("Android Sudoku\nCreated by kianelbo\nVersion 1.1")
//        dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert)
        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    // exit button
    fun exit(view: View) {
        finishAndRemoveTask()
    }
}
