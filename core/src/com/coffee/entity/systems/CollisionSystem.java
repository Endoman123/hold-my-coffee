package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.coffee.util.QuadTree;

/**
 * @author Phillip O'Reggio
 */
public class CollisionSystem extends IteratingSystem {
    QuadTree quadTree;

    public CollisionSystem() {

    }

    public void processEntity(Entity entity, float deltaTime) {

    }
}
