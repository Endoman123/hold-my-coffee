package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Component that stores the vertical and horizontal movement data.
 *
 * @author Jared Tulayan
 */
public class PlayerComponent implements Component, Pool.Poolable {
    // Inputs
    public int up = 0, down = 0, left = 0, right = 0;
    public boolean shoot = false;

    // Shooting timer
    public double shootTimer, shotsPerSecond = 1;

    // Buffs
    public int upFireRate = 0, upBulletDamage = 0, upSpeed = 0;

    // Life stuff that does not belong in the HealthComponent class
    public int lives = 3;
    public boolean revive = false;

    // Player statisitics
    public int shotsFired, shotsHit;

    // Points
    public int score = 0;

    @Override
    public void reset() {
        upBulletDamage = 0;
        upFireRate = 0;
        upSpeed = 0;
        shootTimer = shotsPerSecond = 1;
        score = 0;
    }

    /**
     * Gets the accuracy of the player.
     *
     * @return {@code shotsFired / shotsHit}, or 0 if no bullets were shot
     */
    public float getAccuracy() {
        return shotsFired > 0 ? shotsHit / (float) shotsFired : 0;
    }
}
