package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;

/**
 * Component that stores the vertical and horizontal movement data.
 *
 * @author Jared Tulayan
 */
public class PlayerComponent implements Component {
    // Inputs
    public int up = 0, down = 0, left = 0, right = 0;
    public boolean shoot = false;

    // Shooting timer
    public double shootTimer, bulletsPerSecond = 1;

    // Buffs
    public int upFireRate = 0, upBulletDamage = 0;
}
