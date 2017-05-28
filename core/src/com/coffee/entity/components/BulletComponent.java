package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

/**
 * {@link Component} to keep track of how much damage an {@link Entity}
 * with this component deals.
 *
 * @author Jared Tulayan
 */
public class BulletComponent implements Component, Pool.Poolable {
    public Entity owner;
    public double damage = 10;

    @Override
    public void reset() {
        owner = null;
        damage = 10;
    }
}
