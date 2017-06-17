package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.ColliderComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;

/**
 * {@link EntitySystem} designed specifically for drawing debug shapes.
 * This should not be added to an engine for the actual game.
 *
 * @author Jared Tulayan
 */
public class DebugDrawSystem extends IteratingSystem {
    private final ShapeRenderer RENDERER;
    private final Camera CAMERA;

    public DebugDrawSystem(ShapeRenderer renderer, Viewport viewport) {
        super(Family.one(TransformComponent.class, ColliderComponent.class).get());

        CAMERA = viewport.getCamera();
        RENDERER = renderer;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Transform first
        if (Mapper.TRANSFORM.has(entity)) {
            TransformComponent transform = Mapper.TRANSFORM.get(entity);

            RENDERER.set(ShapeRenderer.ShapeType.Line);
            RENDERER.setColor(Color.GREEN);
            RENDERER.rect(
                    transform.POSITION.x,
                    transform.POSITION.y,
                    transform.ORIGIN.x,
                    transform.ORIGIN.y,
                    transform.SIZE.width,
                    transform.SIZE.height,
                    1,
                    1,
                    (float) transform.rotation
            );

            RENDERER.set(ShapeRenderer.ShapeType.Filled);
            RENDERER.circle(
                    transform.POSITION.x + transform.ORIGIN.x,
                    transform.POSITION.y + transform.ORIGIN.y,
                    3
            );
        }

        // Collider BODY second
        if (Mapper.COLLIDER.has(entity)) {
            ColliderComponent collider = Mapper.COLLIDER.get(entity);

            RENDERER.set(ShapeRenderer.ShapeType.Line);
            RENDERER.setColor(Color.RED);
            RENDERER.polygon(collider.BODY.getTransformedVertices());
        }
    }

    public void update(float dt) {
        RENDERER.setProjectionMatrix(CAMERA.combined);
        RENDERER.begin(ShapeRenderer.ShapeType.Line);

        super.update(dt);

        RENDERER.end();
    }
}
