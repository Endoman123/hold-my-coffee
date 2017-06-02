package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;

/**
 * {@link com.badlogic.ashley.core.Component} representing the health of an entity. Entities like bullets, health power-ups,
 * and shock waves would affect this.
 *
 * @author Phillip O'Reggio
 */
public class HealthComponent implements Component {
    public int health, maxHealth;
    public float invincibilityTimer;
    public float invincibilityDuration;

    /**
     * Creates a {@link HealthComponent} with a set invincibility duration after getting hit, and a maximum health value.
     */
    public HealthComponent() {
        maxHealth = 100;
        health = maxHealth;
        invincibilityDuration = 1;
    }

    /**
     * Gets the percent of health.
     *
     * @return {@code health / maxHealth}
     */
    public float getHealthPercent() {
        return health / (float) maxHealth;
    }
}
