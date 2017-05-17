package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;

/**
 * This {@link EntitySystem} updates both position and rotation
 * by adding the acceleration for both rotation and position to corresponding velocity values,
 * then using velocity values to update transform positions and rotations.
 *
 * @author Jared Tulayan
 */
public class MovementSystem extends IteratingSystem {
    public MovementSystem() {
        super(Family.all(TransformComponent.class, MovementComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Start by getting the transform and movement components
        final TransformComponent curTransform = Mapper.TRANSFORM.get(entity);
        final MovementComponent curMovement = Mapper.MOVEMENT.get(entity);

        // Add the acceleration vector to the movement vector.
        curMovement.MOVEMENT_VECTOR.add(curMovement.ACCELERATION_VECTOR);
        // Check if speed is maxed by checking the squared length against the maxMoveSpeed squared.
        // If it is, normalize it and scale it to max length.
        if (curMovement.MOVEMENT_VECTOR.len2() > curMovement.maxMoveSpeed * curMovement.maxMoveSpeed)
            curMovement.MOVEMENT_VECTOR.nor().scl((float)curMovement.maxMoveSpeed);

        // Add the rotation acceleration to the rotation speed
        curMovement.rotSpeed += curMovement.rotAccel;

        // Check if rotSpeed is greater than maxRotSpeed, and cap it if it is.
        if (Math.abs(curMovement.rotSpeed) > curMovement.maxRotSpeed)
            curMovement.maxRotSpeed = Math.signum(curMovement.rotSpeed) * curMovement.maxRotSpeed;

        // Move and rotate the entity
        curTransform.POSITION.add(curMovement.MOVEMENT_VECTOR);
        curTransform.rotation += curMovement.rotSpeed;

        // Limit rotation to [0, 360) for easy printouts.
        if (curTransform.rotation >= 360)
            curTransform.rotation -= 360;
        if (curTransform.rotation < 0)
            curTransform.rotation += 360;
    }
}
