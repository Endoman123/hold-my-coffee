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
    /**
     * Indexes of nodes that is is approaching. Approaches index at 0 primarily,
     * while gradually transitioning to the index at 1
     * */
    public int currentNode;

    public AIComponent() {
        path = new Array<Vector2>();
        currentNode = 0;
    }

    public void reset() {
        path.clear();
         currentNode = 0;
    }
}
