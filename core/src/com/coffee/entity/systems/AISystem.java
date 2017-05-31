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

        // region State Machine
        switch (AI.state) {
            case 0: // Reset states
                if (AI.actionTimer <= 0) {
                    AI.BEGIN_POS.set(TRANSFORM.POSITION);
                    do {
                        AI.END_POS.set(
                                MathUtils.random(VIEWPORT.getWorldWidth() - TRANSFORM.SIZE.width),
                                MathUtils.random(VIEWPORT.getWorldHeight() * 2.0f / 3.0f, VIEWPORT.getWorldHeight() - TRANSFORM.SIZE.height)
                        );
                    } while (AI.BEGIN_POS.dst2(AI.END_POS) <= 1000);

                    AI.actionTimer = 0;
                    AI.lerpTimer = 0;
                    AI.state = -1;
                } else
                    AI.actionTimer -= deltaTime;
                break;
            case 1: // Attack style 1: Spiral death thing
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.1f) {
                    for (int i = 0; i < 6; i++) {
                        float deg = AI.actionTimer * 7 + i * 60;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        getEngine().addEntity(EntityFactory.createEnemyBall(xPlace, yPlace, deg));
                    }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 200) {
                        AI.actionTimer = 3;
                        AI.state = 0;
                    }
                }
                break;
            case 2: // Attack style 2: Cone
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.15) {
                    if (AI.actionTimer % 2 == 0)
                        for (int i = 0; i < 17; i++) {
                            float deg = 190 + i * 10;
                            float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                            float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                            getEngine().addEntity(EntityFactory.createEnemyBullet(xPlace, yPlace, deg));
                        }
                    else
                        for (int i = 0; i < 16; i++) {
                            float deg = 195 + i * 10;
                            float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                            float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                            getEngine().addEntity(EntityFactory.createEnemyBullet(xPlace, yPlace, deg));
                        }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 20) {
                        AI.actionTimer = 1;
                        if (MathUtils.randomBoolean(0.1f))
                            AI.state = 1;
                        else
                            AI.state = 0;
                    }
                }
                break;
            case 3: // spray in front attack
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.01f) {
                    float deg = 257.5f + MathUtils.random(25);
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    getEngine().addEntity(EntityFactory.createEnemyBulletFast(xPlace, yPlace, deg));

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 60) {
                        AI.actionTimer = 1;
                        AI.state = 0;
                    }
                }

                break;
            case 4: // transparent attack
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.1f) {
                    for (int i = 0; i < 6; i++) {
                        float deg = AI.actionTimer * 7 + i * 60;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        getEngine().addEntity(EntityFactory.createEnemyBulletFade(xPlace, yPlace, deg));
                    }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 100) {
                        AI.actionTimer = 3;
                        AI.state = 0;
                    }
                }

                break;
            default: // Move to position, then begin attacking
                AI.lerpTimer = MathUtils.clamp(AI.lerpTimer + deltaTime * AI.lerpSpeed, 0, 1);
                float perc = MathUtils.sin(AI.lerpTimer * MathUtils.PI / 2.0f);

                TRANSFORM.POSITION.set(
                        MathUtils.lerp(AI.BEGIN_POS.x, AI.END_POS.x, perc),
                        MathUtils.lerp(AI.BEGIN_POS.y, AI.END_POS.y, perc)
                );

                if (AI.lerpTimer == 1) {
                    if (HEALTH.getHealthPercent() >= 0.66) {
                        AI.state = 1;
                        AI.lerpSpeed = 1.6f;
                    } else  if (HEALTH.getHealthPercent() >= 0.43) {
                        AI.state = 3;
                        AI.lerpSpeed = 4f;
                    } else if (HEALTH.getHealthPercent() >= 0.23) {
                            AI.state = 4;
                            AI.lerpSpeed = 1f;
                    } else {
                        AI.state = 2;
                        AI.lerpSpeed = 2.6f;
                    }
                }
        }
        // endregion

        if (HEALTH.health <= 0) {
            getEngine().removeEntity(entity);
            System.out.println("dead boi");
        }
    }
}
