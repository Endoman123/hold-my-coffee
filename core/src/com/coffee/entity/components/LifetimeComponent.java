package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

/**
 * {@link Component} that contains a timer
 * so that this {@link Entity} exists for a certain amount of time in its engine
 *
 * @author Jared Tulayan
 */
public class LifetimeComponent implements Component, Pool.Poolable {
    public double timer;

    public LifetimeComponent() {

    }

    @Override
    public void reset() {

    }
}
