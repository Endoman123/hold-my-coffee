package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.coffee.entity.components.HealthComponent;
import com.coffee.util.Mapper;

/**
 * {@link com.badlogic.ashley.core.EntitySystem EntitySystem} that updates
 * {@link HealthComponent}'s invincibility timer, respawn timer, and clamps the health.
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
        if (health.invincibilityTimer > 0) {
            health.invincibilityTimer -= deltaTime;
            if (health.invincibilityTimer <= 0)
                health.invincibilityTimer = 0;
        }

        // Decrease respawn timer
        if (health.respawnTimer > 0) {
            health.respawnTimer -= deltaTime;
            if (health.respawnTimer <= 0)
                health.respawnTimer = 0;
        }

        health.invincible = health.invincibilityTimer > 0 || health.respawnTimer > 0;

        // Clamp health
        health.health = MathUtils.clamp(health.health, 0, health.maxHealth);
    }
}
