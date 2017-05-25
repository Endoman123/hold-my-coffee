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
        TransformComponent transform = Mapper.TRANSFORM.get(entity);

        // I want to do damage to myself!
        if (transform.POSITION.x < -transform.SIZE.width || transform.POSITION.y < -transform.SIZE.height ||
            transform.POSITION.x > MAP_SIZE.width || transform.POSITION.y > MAP_SIZE.height) {
            getEngine().removeEntity(entity);
            //System.out.println("damage to myself");
        }
    }
}
