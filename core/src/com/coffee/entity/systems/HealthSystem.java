package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.coffee.entity.components.HealthComponent;
import com.coffee.util.Mapper;

/**
 * {@link com.badlogic.ashley.core.EntitySystem} that updates the handles {@link HealthComponent}'s invincibility frames.
 *
 * @author Phillip O'Reggio
 */
public class HealthSystem extends IteratingSystem {
    public HealthSystem() {
        super(Family.one(HealthComponent.class).get());
    }

    public void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = Mapper.HEALTH.get(entity);

        // Decrease invincibility timer
            health.invincibilityTimer = MathUtils.clamp(
                    health.invincibilityTimer - deltaTime,
                    0,
                    health.invincibilityDuration
            );

        // Clamp health
        health.health = MathUtils.clamp(health.health, 0, health.maxHealth);
    }
}
