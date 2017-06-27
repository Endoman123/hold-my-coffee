package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.BulletComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;

import java.awt.*;


/**
 * @author Jared Tulayan
 */
public class BulletSystem extends IteratingSystem {
    private final Dimension MAP_SIZE;

    public BulletSystem(Viewport v) {
        super(Family.one(BulletComponent.class).get());
        MAP_SIZE = new Dimension((int)v.getWorldWidth(), (int)v.getWorldHeight());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
        final BulletComponent BULLET = Mapper.BULLET.get(entity);

        if (BULLET.handler != null)
            BULLET.handler.update(deltaTime);

        boolean
            outsideLowerBounds = TRANSFORM.POSITION.x < -TRANSFORM.SIZE.width || TRANSFORM.POSITION.y < -TRANSFORM.SIZE.height,
            outsideUpperBounds = TRANSFORM.POSITION.x > MAP_SIZE.width || TRANSFORM.POSITION.y > MAP_SIZE.height;

        if (BULLET.despawnTime != -1.0f && (outsideLowerBounds || outsideUpperBounds)) {
            BULLET.timer -= deltaTime;
            if (BULLET.timer <= 0)
                getEngine().removeEntity(entity);
        } else {
            BULLET.timer = BULLET.despawnTime;
        }
    }
}
