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
    public Array<Sprite> sprites;
    public int zIndex;

    public SpriteComponent(Array<Sprite> s) {
        sprites = s;
    }

    public SpriteComponent(Sprite[] s, int z) {
        sprites = new Array<Sprite>(s);
        zIndex = z;
    }

    public SpriteComponent() {
        this(new Array<Sprite>());
    }
}


