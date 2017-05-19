package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;

/**
 * This {@link EntitySystem} updates both position and rotation
 * by using velocity values to update transform positions and rotations.
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

        // Store the real normal vector in a temporary object.
        // This vector requires that we normalize the normal vector and scale it to the move speed magnitude.
        Vector2 move_vec = new Vector2(curMovement.MOVEMENT_NORMAL).nor().scl((float)curMovement.moveSpeed);

        // Move and rotate the entity
        curTransform.POSITION.add(move_vec);
        curTransform.rotation += curMovement.rotSpeed;

        // Limit rotation to [0, 360) for easy printouts.
        if (curTransform.rotation >= 360)
            curTransform.rotation -= 360;
        if (curTransform.rotation < 0)
            curTransform.rotation += 360;
    }
}
