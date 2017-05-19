package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.coffee.entity.components.ColliderComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.QuadTree;

/**
 * @author Phillip O'Reggio
 */
public class CollisionSystem extends IteratingSystem {
    QuadTree quadTree;

    public CollisionSystem() {
        super(Family.all(ColliderComponent.class, TransformComponent.class).get());
    }

    public void processEntity(Entity entity, float deltaTime) {

    }
}
