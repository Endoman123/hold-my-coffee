package com.coffee.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;

/**
 * @author Jared Tulayan
 */
public class OptionsManager {
    private static Preferences opts;
    public static boolean fullscreen = false;
    public static String resolution = "450x800";
    public static float volume = 0.05f;

    /**
     * Initializes the options by getting the prefs file and
     * initializing the settings as per the currently saved options.
     */
    public static void init() {
        if (opts == null) {
            opts = Gdx.app.getPreferences("hmc-opts");

            fullscreen = opts.getBoolean("fullscreen", false);
            resolution = opts.getString("resolution", "450x800");
            volume = opts.getFloat("volume", 0.05f);

            update();
        }
    }

    public static void update() {
        opts.putBoolean("fullscreen", fullscreen);
        opts.putString("resolution", resolution);
        opts.putFloat("volume", volume);

        if (fullscreen) {
            Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();

            Gdx.graphics.setFullscreenMode(mode);
        } else {
            int
                width = Integer.parseUnsignedInt(resolution.substring(0, resolution.indexOf("x"))),
                height = Integer.parseUnsignedInt(resolution.substring(resolution.indexOf("x") + 1));

            Gdx.graphics.setWindowedMode(width, height);
        }

        opts.flush();
    }
}
