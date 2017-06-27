package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * {@link com.badlogic.ashley.core.Component Component} representing the health of an entity. Entities like bullets, health power-ups,
 * and shock waves would affect this.
 *
 * @author Phillip O'Reggio
 */
public class HealthComponent implements Component, Pool.Poolable {
    public int health, maxHealth;
    public float invincibilityTimer, invincibilityDuration, respawnTimer, respawnDuration;
    public boolean invincible;

    /**
     * Creates a {@link HealthComponent} with a set invincibility duration after getting hit, and a maximum health value.
     */
    public HealthComponent() {
        maxHealth = 100;
        health = maxHealth;
        invincibilityDuration = 1;
        respawnDuration = 1;
        invincible = false;
    }

    /**
     * Gets the percent of health.
     *
     * @return {@code health / maxHealth}
     */
    public float getHealthPercent() {
        return health / (float) maxHealth;
    }

    @Override
    public void reset() {
        maxHealth = 100;
        health = maxHealth;
        invincibilityDuration = 1;
        respawnDuration = 1;
        invincible = false;

        invincibilityTimer = 0;
        respawnTimer = 0;
    }
}
