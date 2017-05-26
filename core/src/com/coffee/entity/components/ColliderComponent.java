package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.coffee.util.CollisionHandler;

/**
 * {@link Component} that contains a {@link Polygon} for collison, and a {@link CollisionHandler} for handling
 * the effects of a collision.
 *
 * @author Phillip O'Reggio
 */
public class ColliderComponent implements Component {
    public Polygon body;
    public final CollisionHandler HANDLER;
    public boolean solid;
    public Array<Entity> collidingWith;

    /**
     * Creates component with a polygonal square body of size 32.
     */
    public ColliderComponent(CollisionHandler handler) {
        body = new Polygon(new float[]{
                0,0,
                32, 0,
                32, 32,
                0, 32
        });
        solid = true;
        collidingWith = new Array<Entity>();
        HANDLER = handler;
    }
}
