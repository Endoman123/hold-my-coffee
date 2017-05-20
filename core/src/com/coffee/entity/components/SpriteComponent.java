package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.coffee.entity.systems.DrawSystem;

import java.util.ArrayList;

/**
 * Contains an {@link ArrayList} of sprites that will be drawn in the {@link DrawSystem}.
 * Sprites with lower indices are drawn on the bottom.
 *
 * @author Phillip O'Reggio
 */
public class SpriteComponent implements Component {
    public final Array<Sprite> SPRITES;
    public int zIndex;

    public SpriteComponent(Array<Sprite> s) {
        SPRITES = s;
    }

    public SpriteComponent(Sprite[] s, int z) {
        SPRITES = new Array<Sprite>(s);
        zIndex = z;
    }

    public SpriteComponent() {
        this(new Array<Sprite>());
    }
}


