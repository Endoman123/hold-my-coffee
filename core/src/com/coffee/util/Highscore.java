package com.coffee.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Class that stores both the {@link ObjectMap ObjectMap} and JSON file
 * to store highscores. This class also manages sorting the players and inserting the new high scores.
 *
 * @author Jared Tulayan
 */
public class Highscore {
    private static final ObjectMap<String, Integer> SCORES;
    private static final Json JSON;

    static {
        JSON = new Json();
        SCORES = new ObjectMap<>(10);
    }

    public void save() {
        JSON.toJson(SCORES, ObjectMap.class);
    }
}
