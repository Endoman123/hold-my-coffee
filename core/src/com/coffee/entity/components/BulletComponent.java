package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.coffee.util.BulletHandler;

/**
 * {@link Component} to keep track of how much damage an {@link Entity}
 * with this component deals.
 *
 * @author Jared Tulayan
 */
public class BulletComponent implements Component, Pool.Poolable {
    public BulletHandler handler;
    public float timer, despawnTime;
    public double damage = 10;
    public int state = 0;

    @Override
    public void reset() {
        handler = null;
        damage = 10;
        timer = 0;
        despawnTime = 0;
        state = 0;
    }
}
