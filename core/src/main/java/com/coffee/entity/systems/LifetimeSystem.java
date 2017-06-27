package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.coffee.entity.components.LifetimeComponent;
import com.coffee.util.Mapper;

/**
 * @author Jared Tulayan
 */
public class LifetimeSystem extends IteratingSystem {
    public LifetimeSystem() {
        super(Family.one(LifetimeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LifetimeComponent particle = Mapper.LIFETIME.get(entity);

        particle.timer -= deltaTime;

        if (particle.timer <= 0) {
            getEngine().removeEntity(entity);
        }
    }
}
