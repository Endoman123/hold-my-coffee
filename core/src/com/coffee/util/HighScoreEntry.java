package com.coffee.util;

/**
 * Class to represent an entry in a high score set.
 *
 * @author Phillip O'Reggio
 */
public class HighScoreEntry implements Comparable {
    private int score;
    private String name;
    public static final String NO_NAME = "---";

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public HighScoreEntry(int playerPoints, String n) {
        score = playerPoints;
        name = n;
    }

    public HighScoreEntry() {
        score = 0;
        name = NO_NAME;
    }

    public int compareTo(Object o) {
        int otherScore = ((HighScoreEntry) o).getScore();
        if (otherScore > score)
            return 1;
        else if (otherScore < score)
            return -1;
        else
            return 0;
    }
}
