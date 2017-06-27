package com.coffee.util;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * {@link AssetManager} wrapper class that contains some {@link AssetDescriptor}s
 * to easily load in assets on the fly.
 *
 * @author Jared Tulayan
 */
public class Assets {
    public static final AssetManager MANAGER;

    // Initialize the asset manager by constructing it.
    static {
        MANAGER = new AssetManager();
        MANAGER.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(new InternalFileHandleResolver()));
    }

    /**
     * Subclass containing {@link AssetDescriptor}s for game object textures
     */
    public static class GameObjects {
        public static AssetDescriptor<TextureAtlas> ATLAS = new AssetDescriptor<>("gameobjects/gameobjects.pack", TextureAtlas.class);
    }

    /**
     * Subclass containing {@link AssetDescriptor}s for ui textures
     */
    public static class UI {
        public static AssetDescriptor<TextureAtlas> ATLAS = new AssetDescriptor<>("ui/uiskin.atlas", TextureAtlas.class);
        public static AssetDescriptor<Skin> SKIN = new AssetDescriptor<>("ui/uiskin.json", Skin.class);
    }

    /**
     * Subclass containing {@link AssetDescriptor}s for music and sfx
     */
    public static class Audio {
        public static AssetDescriptor<Music> THEME = new AssetDescriptor<>("sounds/theme.ogg", Music.class);
        public static AssetDescriptor<Sound> LASER_SHOOT = new AssetDescriptor<Sound>("sounds/laser.ogg", Sound.class);
        public static AssetDescriptor<Sound> POWERUP_SOUND = new AssetDescriptor<Sound>("sounds/powerup.ogg", Sound.class);
    }
}
