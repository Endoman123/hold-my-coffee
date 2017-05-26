package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.awt.*;

/**
 * {@link Component} which contains all the data
 * for storing an {@link Entity}’s
 * location, size, rotation, and origin.
 *
 * @author Jared Tulayan
 */
public class TransformComponent implements Component, Pool.Poolable {
    public final Vector2
        POSITION,
        ORIGIN;

    public final Dimension SIZE;

    public double rotation;

    /**
     * Initializes this {@link TransformComponent}
     * so that the entity has a size of 32 by 32
     * with its origin at its center.
     */
    public TransformComponent() {
        POSITION = new Vector2();
        SIZE = new Dimension(32, 32);
        ORIGIN = new Vector2(16, 16);

        rotation = 0;
    }

    @Override
    public void reset() {
        POSITION.setZero();
        SIZE.setSize(32, 32);
        ORIGIN.set(16, 16);

        rotation = 0;
    }
}
