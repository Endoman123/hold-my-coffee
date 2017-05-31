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
    public float timer;
    public double damage = 10;

    @Override
    public void reset() {
        handler = null;
        damage = 10;
        timer = 0;
    }
}
