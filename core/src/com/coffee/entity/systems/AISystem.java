package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.AIComponent;
import com.coffee.entity.components.HealthComponent;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class AISystem extends IteratingSystem {
    public final Viewport VIEWPORT;

    public AISystem(Viewport v) {
        super(Family.all(AIComponent.class).get());
        VIEWPORT = v;
    }

    public void processEntity(Entity entity, float deltaTime) {
        final AIComponent AI = Mapper.AI.get(entity);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
        final MovementComponent MOVE = Mapper.MOVEMENT.get(entity);
        final HealthComponent HEALTH = Mapper.HEALTH.get(entity);
        //states should get harder with higher index numbers
        //TODO Chain attacks at harder difficulties

        // region State Machine
        AI.ACTIONS.get(AI.state).doAction(entity, deltaTime);
        // endregion

        if (HEALTH.health <= 0) {
            getEngine().removeEntity(entity);
            System.out.println("dead boi");
        }
    }
}
