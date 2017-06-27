package com.coffee.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * Class full of functions to use throughout the game.
 *
 * @author Jared Tulayan
 */
public class CoffeeUtils {
    /**
     * Generates a biased random number.
     * @param min minimum range
     * @param max maximum range
     * @param bias favors lower end if bias > 1. Favors upper end if bias < 1.
     */
    public static int biasedRandom(int min, int max, float bias) {
        float r = MathUtils.random();    // random between 0 and 1
        r = (float) Math.pow(r, bias);
        return Math.round(min + (max - min) * r);
    }

    /**
     * Converts a color in the HSV color space to the RGB color space using {@link java.awt.Color#HSBtoRGB Color.HSBtoRGB}.
     * @param h the hue [0 - 360)
     * @param s the saturation [0 - 100]
     * @param v the value/brightness [0 - 100]
     * @return the HSV color in the RGB color space
     */
    public static Color HSVtoRGB(float h, float s, float v) {
        return Color.valueOf(Integer.toHexString(java.awt.Color.HSBtoRGB(h, s / 100f, v / 100f)));
    }
}
