package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * {@link Component} to keep track of how much damage an {@link Entity}
 * with this component deals.
 *
 * @author Jared Tulayan
 */
public class BulletComponent implements Component {
    public double damage = 10;
}
