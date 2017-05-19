package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.coffee.util.CollisionHandler;

/**
 * @author Phillip O'Reggio
 */
public class ColliderComponent implements Component {
    public Polygon body;
    public CollisionHandler handler;
    public boolean solid;
    public Array<Entity> collidingWith;
}
