package com.coffee.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

/**
 * @author Jared Tulayan
 */
@FunctionalInterface
public interface SpawnerHandler {
    public Array<Entity> spawn();
}
