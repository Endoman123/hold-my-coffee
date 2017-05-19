package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

/**
 * {@link Component} which contains all the data
 * for storing an {@link Entity}’s
 * movement vector, movement speed, and rotation speed.
 *
 * @author Jared Tulayan
 */
public class MovementComponent implements Component {
    public final Vector2 MOVEMENT_NORMAL;

    public double rotSpeed, moveSpeed;

    public MovementComponent() {
        MOVEMENT_NORMAL = new Vector2();
    }
}
