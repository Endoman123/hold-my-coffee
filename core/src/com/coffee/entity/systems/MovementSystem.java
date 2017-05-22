package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
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
    private final Viewport VIEWPORT;
    private final ShapeRenderer DEBUG;
    public boolean doDebug = false;

    /**
     * Calls {@link MovementSystem#MovementSystem(Viewport)}
     * with the {@link Viewport} as null
     */
    public MovementSystem() {
        this(null);
    }

    /**
     * Constructs this {@link EntitySystem} with the specified {@link Viewport} for
     * rendering the transform.
     *
     * @param viewport the {@code Viewport} whose camera to use to transform the
     *                 projection matrix of the {@code ShapeRenderer}
     */
    public MovementSystem(Viewport viewport) {
        super(Family.all(TransformComponent.class, MovementComponent.class).get());
        VIEWPORT = viewport;

        if (VIEWPORT != null) {
            DEBUG = new ShapeRenderer();
            doDebug = true;
        } else
            DEBUG = null;
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
