package com.kian.sudoku

class SudokuModel(difficulty: Int) {
    val board: Array<CharArray>
    lateinit var startingBoard: Array<CharArray>
        private set
    var progress: Int
        private set

    fun update(x: Int, y: Int, value: Char) {
        if (board[x][y] == value) return
        if (board[x][y] == '0') progress++ else if (value == '0') progress--
        board[x][y] = value
    }

    fun hasWon(): Boolean {
        // checking duplicates in rows and columns
        for (i in 0..8) {
            val row: MutableSet<Char> = mutableSetOf()
            val col: MutableSet<Char> = mutableSetOf()
            for (j in 0..8) {
                if (row.contains(board[i][j])) return false
                row.add(board[i][j])
                if (col.contains(board[j][i])) return false
                col.add(board[j][i])
            }
        }
        // checking duplicates in 3x3 boxes
        for (corner in boxCorners) {
            val box: MutableSet<Char> = mutableSetOf()
            for (i in 0..2) for (j in 0..2)
                if (box.contains(board[corner.first + i][corner.second + j])) return false
                else box.add(board[corner.first + i][corner.second + j])
        }
        // no duplicates
        return true
    }

    companion object {
        private val prototype = arrayOf(
            "827154396".toCharArray(), "965327148".toCharArray(), "341689752".toCharArray(),
            "593468271".toCharArray(), "472513689".toCharArray(), "618972435".toCharArray(),
            "786235914".toCharArray(), "154796823".toCharArray(), "239841567".toCharArray()
        )

        private val boxCorners = listOf(
            Pair(0, 0), Pair(3, 0), Pair(6, 0),
            Pair(0, 3), Pair(3, 3), Pair(6, 3),
            Pair(0, 6), Pair(3, 6), Pair(6, 6)
        )
    }

    init {
        // creating a permutation
        val letters = mutableListOf('1', '2', '3', '4', '5', '6', '7', '8', '9')
        letters.shuffle()

        // initializing the puzzle
        board = Array(9) { CharArray(9) }
        for (i in 0..8) for (j in 0..8) board[i][j] = letters[prototype[i][j].toInt() - 49]
        // cells to be erased
        val randomsSet: MutableSet<Int> = mutableSetOf()
        while (randomsSet.size < difficulty) randomsSet.add((0..80).random())
        val spots = randomsSet.toList()
        // erasing from the board
        for (i in 0 until difficulty) board[spots[i] / 9][spots[i] % 9] = '0'
        // saving predefined cells
        startingBoard = Array(9) { board[it].clone() }
        progress = 81 - difficulty
    }
}
