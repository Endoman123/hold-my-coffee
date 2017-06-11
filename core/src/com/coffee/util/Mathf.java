package com.coffee.util;

import com.badlogic.gdx.math.MathUtils;

/**
 * Class full of mathematical functions to use throughout the game.
 *
 * @author Jared Tulayan
 */
public class Mathf {
    /**
     * Generates a biased random number.
     * @param min minimum range
     * @param max maximum range
     * @param bias favors lower end if bias > 1. Favors upper end if bias < 1.
     */
    public static int biasedRandom(int min, int max, float bias) { //Dunno where else to put this JAROD
        float r = MathUtils.random();    // random between 0 and 1
        r = (float) Math.pow(r, bias);
        return Math.round(min + (max - min) * r);
    }
}
