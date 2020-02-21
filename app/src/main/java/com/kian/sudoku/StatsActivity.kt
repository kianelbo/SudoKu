package com.kian.sudoku

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_stats.*
import com.kian.sudoku.TimeHandler.Companion.formattedTime

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // showing stats data
        showStats()

        // reset stats
        resetButton.setOnClickListener {
            val builder = AlertDialog.Builder(this@StatsActivity)
            builder.setMessage("Confirm reset?")
            builder.setPositiveButton("Yes") { _, _ ->
                val editor = getSharedPreferences("STATS_PREFS", Context.MODE_PRIVATE).edit()
                editor.putInt("EasyGames", 0)
                editor.putInt("MediumGames", 0)
                editor.putInt("HardGames", 0)
                editor.putLong("EasyTime", 0)
                editor.putLong("MediumTime", 0)
                editor.putLong("HardTime", 0)
                editor.apply()
                showStats()
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MenuActivity::class.java))
    }

    private fun showStats() {
        val sPrefs = getSharedPreferences("STATS_PREFS", Context.MODE_PRIVATE)

        val easyGames = sPrefs.getInt("EasyGames", 0)
        val mediumGames = sPrefs.getInt("MediumGames", 0)
        val hardGames = sPrefs.getInt("HardGames", 0)
        val totalGames = easyGames + mediumGames + hardGames
        easyGamesView.text = easyGames.toString()
        mediumGamesView.text = mediumGames.toString()
        hardGamesView.text = hardGames.toString()
        totalGamesView.text = totalGames.toString()

        val easyTime = sPrefs.getLong("EasyTime", 0)
        val mediumTime = sPrefs.getLong("MediumTime", 0)
        val hardTime = sPrefs.getLong("HardTime", 0)
        val totalTime: Long = easyTime * easyGames + mediumTime * mediumGames + hardTime * hardGames
        easyTimeView.text = formattedTime(easyTime)
        mediumTimeView.text = formattedTime(mediumTime)
        hardTimeView.text = formattedTime(hardTime)
        totalTimeView.text = formattedTime(totalTime)
    }
}
