package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;

/**
 * {@link com.badlogic.ashley.core.Component} representing the health of an entity. Entities like bullets, health power-ups,
 * and shock waves would affect this.
 *
 * @author Phillip O'Reggio
 */
public class HealthComponent implements Component {
    public final int MAX_HEALTH;
    public int health;
    public float invincibilityTimer;
    public final float INVINCIBILITY_DURATION;
    public boolean isInvicible;

    /**
     * Creates a {@link HealthComponent} with a set invincibility duration after getting hit, and a maximum health value.
     * @param duration invincibility duration
     */
    public HealthComponent(float duration, int maxHealth) {
        INVINCIBILITY_DURATION = duration;
        MAX_HEALTH = maxHealth;
        health = MAX_HEALTH;
    }

    /**
     * Creates a {@link HealthComponent} with a set invincibility duration after getting hit, and an health amount.
     * @param maxHealth maximum health
     * @param duration invincibility duration
     */
    public HealthComponent(int maxHealth, float duration) {
        MAX_HEALTH = maxHealth;
        health = MAX_HEALTH;
        INVINCIBILITY_DURATION = duration;
    }

    /**
     * Gets the percent of health.
     *
     * @return {@code health / MAX_HEALTH}
     */
    public float getHealthPercent() {
        return health / (float) MAX_HEALTH;
    }
}
