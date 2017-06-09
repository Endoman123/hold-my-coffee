package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.*;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class AISystem extends IteratingSystem {
    public final Viewport VIEWPORT;
    public final Vector2 DEF, TEMP;


    public AISystem(Viewport v) {
        super(Family.all(AIComponent.class).get());
        VIEWPORT = v;
        DEF = new Vector2(-999, -999);
        TEMP = new Vector2();
    }

    public void processEntity(Entity entity, float deltaTime) {
        final AIComponent AI = Mapper.AI.get(entity);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
        final MovementComponent MOVE = Mapper.MOVEMENT.get(entity);
        final HealthComponent HEALTH = Mapper.HEALTH.get(entity);
        //states should get harder with higher index numbers
        //TODO Chain attacks at harder difficulties
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
            case 1: // Charge to explosion
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.01f) {
                    getEngine().addEntity(EntityFactory.createEnemyBulletExploding(TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x, TRANSFORM.POSITION.y + 32, 0));

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer >= 1) {
                        AI.actionTimer = 4;
                        AI.state = 0;
                    }
                }

                break;
            case 2: // Simple spiral
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

                    if (AI.actionTimer == 150) {
                        AI.actionTimer = 3;
                        if (MathUtils.randomBoolean(0.1f))
                            AI.state = 1;
                        else
                            AI.state = 0;
                    }
                }
                break;
            case 3: // Laser
                AI.fireTimer += deltaTime;

                if (AI.TARGET_LOC.epsilonEquals(DEF, 1)) {
                    final ImmutableArray<Entity> PLAYERS = getEngine().getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
                    AI.TARGET_LOC.set(VIEWPORT.getWorldWidth() / 2f, VIEWPORT.getWorldHeight() / 2f);

                    if (PLAYERS.size() != 0) {
                        final TransformComponent PLAYER_TRANS = Mapper.TRANSFORM.get(PLAYERS.first());

                        AI.TARGET_LOC.set(PLAYER_TRANS.POSITION).add(PLAYER_TRANS.ORIGIN);
                    }
                }


                if (AI.fireTimer >= 0.001f) {
                    TEMP.set(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);

                    float theta = MathUtils.atan2(AI.TARGET_LOC.y - TEMP.y, AI.TARGET_LOC.x - TEMP.x);
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(theta);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(theta);

                    getEngine().addEntity(EntityFactory.createWeakFastEnemyBullet(xPlace, yPlace, theta * MathUtils.radDeg));

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 250) {
                        AI.TARGET_LOC.set(DEF);
                        AI.actionTimer = 1;
                        AI.state = 0;
                    }
                }

                break;
            case 4: // invisible homing bullets
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.1f) {
                    for (int i = 0; i < 6; i++) {
                        float deg = 220 + i * 20;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        getEngine().addEntity(EntityFactory.createHomingEnemyBullet(xPlace, yPlace, deg));
                    }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 1) {
                        AI.actionTimer = 3;
                        AI.state = 0;
                    }
                }
                break;
            case 5: // Spiral thing part 2
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
            case 6: // Cone
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.15) {
                    if (AI.actionTimer % 2 == 0)
                        for (int i = 0; i < 17; i++) {
                            float deg = 190 + i * 10;
                            float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                            float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                            getEngine().addEntity(EntityFactory.createEnemyBulletSlows(xPlace, yPlace, deg));
                        }
                    else
                        for (int i = 0; i < 16; i++) {
                            float deg = 195 + i * 10;
                            float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                            float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                            getEngine().addEntity(EntityFactory.createEnemyBulletSlows(xPlace, yPlace, deg));
                        }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 20) {
                        if (MathUtils.randomBoolean(0.1f)) {
                            AI.actionTimer = 0;
                            AI.state = 4;
                        } else {
                            AI.actionTimer = 1;
                            AI.state = 0;
                        }
                    }
                }
                break;
            case 7: // invisible homing bullets
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.1f) {
                    for (int i = 0; i < 6; i++) {
                        float deg = 220 + i * 20;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        getEngine().addEntity(EntityFactory.createHomingEnemyBullet(xPlace, yPlace, deg));
                    }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 1) {
                        AI.actionTimer = 3;
                        AI.state = 0;
                    }
                }
                break;
            case 8: // Tougher homing
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.1f) {
                    for (int i = 0; i < 6; i++) {
                        float deg = AI.actionTimer * 7 + i * 60;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        getEngine().addEntity(EntityFactory.createHomingEnemyBullet(xPlace, yPlace, deg));
                    }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 9) {
                        AI.actionTimer = 2;
                        if (MathUtils.randomBoolean(0.1f))
                            AI.state = 9;
                        else
                            AI.state = 0;
                    }
                }
                break;
            case 9: // Shoot bullet laser bursts
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.1f) {
                    final Vector2
                            WORLD_CENTER = new Vector2(VIEWPORT.getWorldWidth() / 2, VIEWPORT.getWorldHeight() / 2),
                            TRANS_CENTER = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);

                    float theta = MathUtils.radDeg * MathUtils.atan2(WORLD_CENTER.y - TRANS_CENTER.y, WORLD_CENTER.x - TRANS_CENTER.x);

                    for (int i = 0; i < 3; i++) {
                        float deg = theta + i * 30 - 30;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        getEngine().addEntity(EntityFactory.createEnemyBulletExplodingMoving(xPlace, yPlace, deg));
                    }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 1) {
                        AI.actionTimer = 4;
                        AI.state = 0;
                    }
                }
                break;
            case 10: // Shifting spiral
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.25f) {
                    for (int i = 0; i < 6; i++) {
                        float deg = AI.actionTimer * 7 + i * 60;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        getEngine().addEntity(EntityFactory.createEnemyBallShifter(xPlace, yPlace, deg));
                    }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 50) {
                        AI.actionTimer = 3;
                        if (MathUtils.randomBoolean(0.1f))
                            AI.state = 3;
                        else
                            AI.state = 0;
                    }
                }
                break;
            case 11: // Not-So-Simple spiral
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.2f) {
                    for (int i = 0; i < 5; i++) {
                        float deg = AI.actionTimer * 7 + i * 72;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        getEngine().addEntity(EntityFactory.createEnemyBallExploding(xPlace, yPlace, deg));
                    }

                    AI.fireTimer = 0;
                    AI.actionTimer++;

                    if (AI.actionTimer == 75) {
                        AI.actionTimer = 3;
                        if (MathUtils.randomBoolean(0.1f))
                            AI.state = 1;
                        else
                            AI.state = 0;
                    }
                }
                break;

            case 12: // Not-So-Fun spiral
                AI.fireTimer += deltaTime;

                if (AI.fireTimer >= 0.001f) {
                    float offset = (int) AI.actionTimer / 20 * 10;
                    for (int i = 0; i < 20; i++) {
                        float deg = offset + 5 + i * 18f;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degRad);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degRad);

                        final Entity BALL = EntityFactory.createEnemyBall(xPlace, yPlace, deg);
                        final BulletComponent BULLET = Mapper.BULLET.get(BALL);
                        final MovementComponent MOVEMENT = Mapper.MOVEMENT.get(BALL);
                        MOVEMENT.moveSpeed = 10;
                        /*BULLET.handler = (float dt) -> {
                            if (MOVEMENT.moveSpeed > 3) {
                                MOVEMENT.moveSpeed -= dt * 3;
                                if (MOVEMENT.moveSpeed <= 3)
                                    MOVEMENT.moveSpeed = 3;
                            }
                        };*/
                        getEngine().addEntity(BALL);
                    }
                    if (AI.fireTimer >= 0.002f) {
                        for (int i = 0; i < 20; i++) {
                            float deg = offset + i * 18f;
                            float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degRad);
                            float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degRad);

                            final Entity BALL = EntityFactory.createEnemyBall(xPlace, yPlace, deg);
                            final BulletComponent BULLET = Mapper.BULLET.get(BALL);
                            final MovementComponent MOVEMENT = Mapper.MOVEMENT.get(BALL);
                            MOVEMENT.moveSpeed = 10;
                        /*BULLET.handler = (float dt) -> {
                            if (MOVEMENT.moveSpeed > 3) {
                                MOVEMENT.moveSpeed -= dt * 3;
                                if (MOVEMENT.moveSpeed <= 3)
                                    MOVEMENT.moveSpeed = 3;
                            }
                        };*/
                            getEngine().addEntity(BALL);
                        }

                        AI.fireTimer = 0;
                        AI.actionTimer++;

                        if (AI.actionTimer == 7000) {
                            AI.actionTimer = 3;
                            if (MathUtils.randomBoolean(0.1f))
                                AI.state = 1;
                            else
                                AI.state = 0;
                        }
                    }
                }
                break;
            case 13: // Fake out
                    AI.actionTimer = .05f;
                    AI.state = 0;
                break;
            default: // Move to position, then begin attacking
                AI.lerpTimer = MathUtils.clamp(AI.lerpTimer + deltaTime * AI.lerpSpeed, 0, 1);
                float perc = MathUtils.sin(AI.lerpTimer * MathUtils.PI / 2.0f);

                TRANSFORM.POSITION.set(
                        MathUtils.lerp(AI.BEGIN_POS.x, AI.END_POS.x, perc),
                        MathUtils.lerp(AI.BEGIN_POS.y, AI.END_POS.y, perc)
                );

                if (AI.lerpTimer == 1) {
                    if (HEALTH.getHealthPercent() >= 0.75) {
                        AI.state = 12;//MathUtils.random(1, 4);
                        AI.lerpSpeed = 1.6f;
                    } else if (HEALTH.getHealthPercent() >= 0.50) {
                        AI.state = MathUtils.random(2, 6);
                        AI.lerpSpeed = 2.4f;
                    } else if (HEALTH.getHealthPercent() >= 0.25) {
                        if (MathUtils.randomBoolean(.1f))
                            AI.state = 12; //Fake out
                        else
                            AI.state = MathUtils.random(1, 7);
                        AI.lerpSpeed = 3.2f;
                    } else if (HEALTH.getHealthPercent() >= 0.1125) {
                        if (MathUtils.randomBoolean(.3f))
                            AI.state = 12; //Fake out
                        else
                            AI.state = MathUtils.random(6, 11);
                        AI.lerpSpeed = 4f;
                    } else {
                        if (MathUtils.randomBoolean(.4f))
                            AI.state = 12; //Fake out
                        else
                            AI.state = MathUtils.random(1, 11);
                        AI.lerpSpeed = MathUtils.random(2f, 4.8f);
                    }
                }
        }
        // endregion

        if (HEALTH.health <= 0)
            getEngine().removeEntity(entity);
    }
}
