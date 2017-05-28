package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.coffee.util.CollisionHandler;

/**
 * {@link Component} that contains a {@link Polygon} for collison, and a {@link CollisionHandler} for handling
 * the effects of a collision.
 *
 * @author Phillip O'Reggio
 */
public class ColliderComponent implements Component, Pool.Poolable {
    public final Polygon BODY;
    public CollisionHandler handler;
    public boolean solid;
    public Array<Entity> collidingWith;

    /**
     * Creates component with a polygonal square BODY of size 32.
     */
    public ColliderComponent() {
        BODY = new Polygon(new float[]{
                0,0,
                32, 0,
                32, 32,
                0, 32
        });
        solid = true;
        collidingWith = new Array<Entity>();
    }

    @Override
    public void reset() {
        BODY.setVertices(new float[]{
                0,0,
                32, 0,
                32, 32,
                0, 32
        });
        BODY.setRotation(0);
        solid = true;
        collidingWith.clear();
    }
}
