package com.kian.pitproject.sudoku;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SudokuModel {
    private static final String[] prototypeModel = {"827154396", "965327148", "341689752",
            "593468271", "472513689", "618972435",
            "786235914", "154796823", "239841567"};
    private String solvedModel[];
    private String startingModel[];
    private String statusModel[];
    private int progress;

    public SudokuModel(int difficulty) {
        List<Character> letters = Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9');
        Collections.shuffle(letters);

        //initializing the puzzle
        solvedModel = new String[9];
        for (int i = 0; i < 9; i++) {
            char[] line = prototypeModel[i].toCharArray();
            for (int j = 0; j < 9; j++)
                line[j] = letters.get(Integer.parseInt(String.valueOf(line[j])) - 1);
            solvedModel[i] = new String(line);
        }

        //cells to be erased
        Random random = new Random();
        Set<Integer> intSet = new HashSet<>();
        while (intSet.size() < difficulty)
            intSet.add(random.nextInt(80) + 1);
        int[] ints = new int[intSet.size()];
        Iterator<Integer> iterator = intSet.iterator();
        for (int i = 0; iterator.hasNext(); ++i)
            ints[i] = iterator.next();

        //making the puzzle
        statusModel = new String[9];
        System.arraycopy(solvedModel, 0, statusModel, 0, 9);
        for (int i = 0; i < difficulty; i++)
            setCell(ints[i] / 9, ints[i] % 9, '0');

        startingModel = new String[9];
        System.arraycopy(statusModel, 0, startingModel, 0, 9);

        progress = 81 - difficulty;
    }

    public String[] getStatusModel() {
        return statusModel;
    }

    public String[] getStartingModel() {
        return startingModel;
    }

    public void update(int x, int y, char value) {
        if (statusModel[x].charAt(y) == value) return;
        if (statusModel[x].charAt(y) == '0') progress++;
        else if (value == '0') progress--;
        setCell(x, y, value);
    }

    public boolean hasWon() {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (statusModel[i].charAt(j) != solvedModel[i].charAt(j)) return false;
        return true;
    }

    public int getProgress() {
        return progress;
    }

    private void setCell(int x, int y, char value) {
        StringBuilder builder = new StringBuilder(statusModel[x]);
        builder.setCharAt(y, value);
        statusModel[x] = builder.toString();
    }


}
