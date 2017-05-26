package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.coffee.entity.components.AIComponent;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class AISystem extends IteratingSystem {

    public AISystem() {
        super(Family.all(AIComponent.class).get());
    }


    public void processEntity(Entity entity, float deltaTime) {
        AIComponent AI = Mapper.AI.get(entity);
        TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
        MovementComponent MOVE = Mapper.MOVEMENT.get(entity);

        //if ((TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x) == AI.path.get())
    }


}
