package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * @author Phillip O'Reggio
 */
public class AIComponent implements Component, Pool.Poolable{
    public Array<Vector2> path;
    public int currentNode;

    public void AIComponent() {
        path = new Array<Vector2>();
    }

    public void reset() {
        path.clear();
    }
}
