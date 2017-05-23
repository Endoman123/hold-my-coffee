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
        health = maxHealth;
    }

    /**
     * Creates a {@link HealthComponent} with a set invincibility duration after getting hit, and an health amount.
     * @param maxHealth maximum health
     * @param duration invincibility duration
     */
    public HealthComponent(int maxHealth, float duration) {
        MAX_HEALTH = maxHealth;
        health = maxHealth;
        INVINCIBILITY_DURATION = duration;
    }

    /**
     * Deals damage by subtracting the damage value from health. Afterwards, it toggles isInvincible to true.
     * @param damage amount subtracted from health.
     */
    public void dealDamage(int damage) {
        health -= damage;
        isInvicible = true;
    }

}
