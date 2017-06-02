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
    public double shootTimer, bulletsPerSecond = 1;

    // Buffs
    public int upFireRate = 0, upBulletDamage = 0, upSpeed = 0;

    // Life stuff that does not belong in the HealthComponent class
    public int lives = 3;
    public boolean revive = false;

    @Override
    public void reset() {
        upBulletDamage = 0;
        upFireRate = 0;
        upSpeed = 0;
        shootTimer = bulletsPerSecond = 1;
    }
}
