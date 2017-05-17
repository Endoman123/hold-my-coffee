package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

/**
 * {@link Component} which contains all the data
 * for storing an {@link Entity}’s
 * movement vector, movement acceleration, movement speed, rotation acceleration, and rotation speed.
 *
 * @author Jared Tulayan
 */
public class MovementComponent implements Component {
    public final Vector2
        MOVEMENT_VECTOR,
        ACCELERATION_VECTOR;

    public double
        rotSpeed,
        rotAccel,
        accelMagnitude,
        maxRotSpeed,
        maxMoveSpeed;

    public MovementComponent() {
        MOVEMENT_VECTOR = new Vector2();
        ACCELERATION_VECTOR = new Vector2();
        maxMoveSpeed = 1;
    }
}
