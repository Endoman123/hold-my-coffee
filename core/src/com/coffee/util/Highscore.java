package com.coffee.util;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.HashMap;

/**
 * Class that stores both the {@link HashMap} and JSON file
 * to store highscores. This class also manages sorting the players and inserting the new high scores.
 *
 */
public class Highscore {
    private static final ArrayMap<String, Integer> SCORES;
    private static final Json JSON;

    static {
        JSON = new Json();
        SCORES = new ArrayMap<>(10);
    }

    public void save() {
        JSON.toJson(SCORES, ObjectMap.class);
    }
}
