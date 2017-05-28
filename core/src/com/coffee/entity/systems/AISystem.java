package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
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
        int choice = 1;

        if (AI.lerpTimer == 1) {
            System.out.println("burp");
            AI.actionTimer = MathUtils.clamp(AI.actionTimer + deltaTime, 0, 5);

            // region Perform Action
            switch (choice) {
                case 1:
                    AI.fireTimer = MathUtils.clamp(AI.fireTimer + deltaTime, 0, 1);

                    if (AI.fireTimer == 1) {
                        for (int i = 0; i < 9; i++) {
                            getEngine().addEntity(EntityFactory.createEnemyBullet(
                                    TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 8,
                                    TRANSFORM.POSITION.y + TRANSFORM.SIZE.height + 10,
                                    180 + i * 20,
                                    entity
                            ));
                        }
                        AI.fireTimer = 0;
                    }

                    if (AI.actionTimer == 5) {
                        AI.actionTimer = 0;
                        AI.lerpTimer = 0;

                        AI.BEGIN_POS.set(TRANSFORM.POSITION);
                        AI.END_POS.set(
                                MathUtils.random(VIEWPORT.getWorldWidth() - TRANSFORM.SIZE.width),
                                MathUtils.random(TRANSFORM.SIZE.height, VIEWPORT.getWorldHeight() * 2.0f / 3.0f)
                        );
                    }
                    break;
            }
        }

        AI.lerpTimer = MathUtils.clamp(AI.lerpTimer + deltaTime, 0, 1);
        float perc = MathUtils.sin(AI.lerpTimer * MathUtils.PI / 2.0f);

        TRANSFORM.POSITION.set(
                MathUtils.lerp(AI.BEGIN_POS.x, AI.END_POS.x, perc),
                MathUtils.lerp(AI.BEGIN_POS.y, AI.END_POS.y, perc)
        );

        if (HEALTH.health <= 0) {
            getEngine().removeEntity(entity);
            System.out.println("dead boi");
        }
    }
}
