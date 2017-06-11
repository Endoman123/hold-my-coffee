package com.coffee.util;

/**
 * @author Phillip O'Reggio
 */
public class HighScoreEntry implements Comparable{
    private int points;
    private String name;

    public int getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public HighScoreEntry(int playerPoints, String n) {
        points = playerPoints;
        name = n;
    }

    public HighScoreEntry() {
        points = -999;
        name = "<undefined>";
    }

    public int compareTo(Object o) {
        if (((HighScoreEntry) o).getPoints() > points) return -1;
        else if (((HighScoreEntry) o).getPoints() < points) return 1;
        else return 0;
    }
}
