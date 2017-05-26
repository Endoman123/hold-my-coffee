package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.PooledLinkedList;

/**
 * @author Phillip O'Reggio
 */
public class AIComponent implements Component, Pool.Poolable{
    public PooledLinkedList<Vector2> path;

    public void AIComponent() {
        path = new PooledLinkedList<Vector2>(0);
    }

    public void reset() {
        path.clear();
    }
}
