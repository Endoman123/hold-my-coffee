package com.coffee.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * @author Jared Tulayan
 */
public class HighScore {
    public static final int SIZE = 10;
    private static final HighScoreEntry[] SCORES; // TODO change to builtin array
    private static Json json;
    private static FileHandle scoreFile;
    private static boolean init = false;

    static {
        SCORES = new HighScoreEntry[] {
            new HighScoreEntry(),
            new HighScoreEntry(),
            new HighScoreEntry(),
            new HighScoreEntry(),
            new HighScoreEntry(),
            new HighScoreEntry(),
            new HighScoreEntry(),
            new HighScoreEntry(),
            new HighScoreEntry(),
            new HighScoreEntry(),
        };
    }

    public static void init() {
        if (!init) {
            json = new Json();
            scoreFile = Gdx.files.local("hold_my_coffee_highscores.json");

            if (scoreFile.exists()) {
                HighScoreEntry[] jsonEntries = json.fromJson(HighScoreEntry[].class, scoreFile.readString());

                System.arraycopy(jsonEntries, 0, SCORES, 0, SIZE);
            }

            init = true;
        }
    }

    /**
     * If the specified score is larger than the lowest score in the list,
     * the score is inserted by pulling current entries down until the entry can be placed.
     */
    public static void insert(HighScoreEntry e) {
        int score = e.getScore();

        // Don't even attempt if it isn't higher than the lowest score
        if (score < getLowest().getScore())
            return;

        // Pull down entries until it can be placed.
        for (int i = SIZE - 1; i > 0; i--) {
            if (SCORES[i - 1].getScore() < score)
                SCORES[i] = SCORES[i - 1];
            else {
                SCORES[i] = e;
                return;
            }
        }

        // High score I guess
        SCORES[0] = e;
    }

    public static HighScoreEntry get(int i) {
        return SCORES[i];
    };

    public static HighScoreEntry getLowest() {
        for (int i = SIZE - 1; i > 0; i--) {
            if (!HighScoreEntry.NO_NAME.equals(SCORES[i].getName()))
                return SCORES[i];
        }

        return SCORES[0];
    }

    public static void save() {
        scoreFile.writeString(json.prettyPrint(SCORES), false);
    }
}
