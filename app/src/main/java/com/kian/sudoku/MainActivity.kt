package com.kian.sudoku

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var lastClickedX = 0
    var lastClickedY = 0
    private var timeHandler: TimeHandler? = null
    lateinit var cells: Array<Array<Cell?>>
    private lateinit var digits: Array<Button?>
    private lateinit var model: SudokuModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // initializing the model and timer
        timeHandler = TimeHandler(timeView)
        val difficulty: Int = intent.getIntExtra("difficulty", -1)
        if (difficulty == -1) {
            val savePrefs = getSharedPreferences("SAVE_PREFS", Context.MODE_PRIVATE)
            model = Gson().fromJson(savePrefs.getString("SavedModel", ""), SudokuModel::class.java)
            timeHandler!!.setSavedMillis(savePrefs.getLong("SavedTimer", 0))
        } else model = SudokuModel(difficulty)

        // puzzle cells
        cells = Array(9) { arrayOfNulls<Cell>(9) }
        val rowLp = TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT)
        val cellLp = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT)
        for (i in 0..8) {
            val row = TableRow(this)
            for (j in 0..8) {
                cells[i][j] = Cell(this, i, j)
                row.addView(cells[i][j], cellLp)
            }
            tableLayout.addView(row, rowLp)
        }

        // input digit buttons
        digits = arrayOf(
            button1, button2, button3, button4, button5, button6, button7, button8, button9
        )
        for (i in 0..8)
            digits[i]!!.setOnClickListener {
                cells[lastClickedX][lastClickedY]!!.text = (i + 1).toString()
                model.update(lastClickedX, lastClickedY, (i + 49).toChar())
                updateProgress()
            }
        buttonClear.setOnClickListener {
            cells[lastClickedX][lastClickedY]!!.text = ""
            model.update(lastClickedX, lastClickedY, '0')
            updateProgress()
        }
        buttonClear.setOnLongClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Confirm reset?")
            builder.setPositiveButton("Yes") { _, _ ->
                for (i in 0..8) for (j in 0..8) if (cells[i][j]!!.hasOnClickListeners()) {
                    cells[i][j]!!.text = ""
                    model.update(i, j, '0')
                }
                updateProgress()
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
            false
        }

        // predefined cells
        for (i in 0..8) for (j in 0..8) if (model.startingBoard[i][j] != '0') {
            cells[i][j]!!.text = model.startingBoard[i][j].toString()
            cells[i][j]!!.setOnClickListener(null)
            cells[i][j]!!.setTypeface(null, Typeface.BOLD)
            cells[i][j]!!.setTextColor(Color.BLUE)
        }
        // first cell to be selected on start
        outer@ for (i in 0..8) for (j in 0..8) if (model.startingBoard[i][j] == '0') {
            cells[i][j]!!.performClick()
            break@outer
        }

        // loading saved game cell values
        if (difficulty == -1)
            for (i in 0..8) for (j in 0..8) if (cells[i][j]!!.hasOnClickListeners())
                cells[i][j]?.text =
                    if (model.board[i][j] != '0') model.board[i][j].toString() else ""

        // initial progress
        updateProgress()
    }

    private fun updateProgress() {
        progressView.text = String.format("%d/%d", model.progress, 81)
        // check win
        if (model.progress == 81 && model.hasWon()) {
            // updating stats
            val statsPrefs = getSharedPreferences("STATS_PREFS", Context.MODE_PRIVATE)
            val statsEditor = statsPrefs.edit()
            val previousAvg = statsPrefs.getLong(model.difficulty + "Time", 0)
            var games = statsPrefs.getInt(model.difficulty + "Games", 0)
            val newAvg = (previousAvg * games + timeHandler!!.elapsedTime / 1000) / ++games
            statsEditor.putLong(model.difficulty + "Time", newAvg)
            statsEditor.putInt(model.difficulty + "Games", games)
            statsEditor.apply()

            // showing win message
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Congratulations!\nCompleted on " + timeView.text)
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    val saveEditor = getSharedPreferences("SAVE_PREFS", Context.MODE_PRIVATE).edit()
                    saveEditor.putBoolean("Saved", false)
                    saveEditor.apply()
                    val intent = Intent(this@MainActivity, MenuActivity::class.java)
                    startActivity(intent)
                }
            builder.create().show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        val prefsEditor = getSharedPreferences("SAVE_PREFS", Context.MODE_PRIVATE).edit()
        prefsEditor.putString("SavedModel", Gson().toJson(model))
        prefsEditor.putLong("SavedTimer", timeHandler!!.elapsedTime)
        prefsEditor.putBoolean("Saved", true)
        prefsEditor.apply()
    }


    inner class Cell(context: Context?, x: Int, y: Int) :
        AppCompatTextView(context) {
        init {
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            gravity = View.TEXT_ALIGNMENT_CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            setBackgroundResource(
                if ((x / 3 + 1) * (y / 3 + 1) % 2 == 1 || (x / 3 + 1) * (y / 3 + 1) == 4)
                    R.drawable.cell_border2
                else R.drawable.cell_border1
            )
            setOnClickListener {
                cells[lastClickedX][lastClickedY]!!.setBackgroundResource(
                    if ((lastClickedX / 3 + 1) * (lastClickedY / 3 + 1) % 2 == 1 || (lastClickedX / 3 + 1) * (lastClickedY / 3 + 1) == 4)
                        R.drawable.cell_border2
                    else R.drawable.cell_border1
                )
                setBackgroundColor(Color.YELLOW)
                lastClickedX = x
                lastClickedY = y
            }
        }
    }
}