package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.coffee.entity.systems.DrawSystem;

import java.util.ArrayList;

/**
 * Contains an {@link ArrayList} of sprites that will be drawn in the {@link DrawSystem}.
 * Sprites with lower indices are drawn on the bottom.
 *
 * @author Phillip O'Reggio
 */
public class SpriteComponent implements Component, Pool.Poolable {
    public final Array<Sprite> SPRITES;
    public int zIndex;

    /**
     * Initializes the {@link Array<Sprite>} to store the sprites
     * and sets the z-index to 0.
     */
    public SpriteComponent() {
        SPRITES = new Array<Sprite>();
        zIndex = 0;
    }

    @Override
    public void reset() {
        SPRITES.clear();
        zIndex = 0;
    }
}


