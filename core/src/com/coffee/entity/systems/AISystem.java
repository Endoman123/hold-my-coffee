package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.AIComponent;
import com.coffee.entity.components.HealthComponent;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.AIState;
import com.coffee.util.BossActions;
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
        // states should get harder with higher index numbers
        // TODO Chain attacks at harder difficulties

        // region State Machine
        switch (AI.curState) {
            case SCHEDULING:
                if (HEALTH.getHealthPercent() >= 0.75) { // 75%+
                    // Create a base for all attacks
                    if (MathUtils.randomBoolean(0.5f)) {
                        AI.TASKS.add(new BossActions.SimpleSpiralAttack(getEngine()));
                    } else {
                        AI.TASKS.add(new BossActions.TempestBloom(getEngine()));
                    }

                    // Add some spices
                    final BossActions.ActionSequence SEQ = new BossActions.ActionSequence(0.3f);
                    int i = 0;
                    while (i < 5) {
                        if (MathUtils.randomBoolean(0.7f)) { // Have a chance that you don't add anything at all
                            if (MathUtils.randomBoolean(0.5f)) { // Add some random homing stuff
                                SEQ.addAction(new BossActions.InvisibleHomingBulletsAttack(getEngine()));
                                i++;
                            } else { // Add some random cannon stuff
                                SEQ.addAction(new BossActions.ShotgunSpray(getEngine()));
                                i++;
                            }
                        } else
                            i++;
                    }

                    SEQ.parallel = true;
                    AI.TASKS.add(SEQ);

                    if (MathUtils.randomBoolean(0.5f)) {
                        AI.TASKS.add(new BossActions.SimpleLaserAttack(getEngine(), VIEWPORT));
                    }

                    if (MathUtils.randomBoolean(0.5f)) {
                        Vector2 move = new Vector2();
                        generateRandomMoveTarget(TRANSFORM, move);
                        AI.TASKS.add(new BossActions.Move(0.5f, move));
                    }
                } else if (HEALTH.getHealthPercent() >= 0.50) {  // 50% - 75
                    // Create a base for all attacks
                    if (MathUtils.randomBoolean(0.5f)) {
                        AI.TASKS.add(new BossActions.SimpleConeAttack(getEngine()));

                        if (MathUtils.randomBoolean(0.7f)) { // Have a chance that you don't add anything at all
                            if (MathUtils.randomBoolean(0.2f)) { // Add some random homing stuff
                                final BossActions.Action ACTION = new BossActions.HomingBulletCircleAttack(getEngine());
                                ACTION.parallel = true;
                                AI.TASKS.add(ACTION);
                            } else { // Add some random cannon stuff
                                final BossActions.Action ACTION = new BossActions.HelixLaserAttack(getEngine(), VIEWPORT);
                                ACTION.parallel = true;
                                AI.TASKS.add(ACTION);
                            }
                        }

                        if (MathUtils.randomBoolean(0.5f)) {
                            AI.TASKS.add(new BossActions.TripleLaserBallAttack(getEngine(), VIEWPORT));
                        }
                    } else {
                        AI.TASKS.add(new BossActions.SpiralColumnAttack(getEngine()));
                        AI.TASKS.add(new BossActions.DoNothing(5f));
                    }

                    if (MathUtils.randomBoolean(0.5f)) {
                        Vector2 move = new Vector2();
                        generateRandomMoveTarget(TRANSFORM, move);
                        AI.TASKS.add(new BossActions.Move(0.5f, move));
                    }
                } else if (HEALTH.getHealthPercent() >= 0.1125) {  // 11.25% - 25%
                    // Create a base for all attacks
                    if (MathUtils.randomBoolean(0.66f)) {
                        if (MathUtils.randomBoolean(0.33f)) {
                            AI.TASKS.add(new BossActions.ShiftingSpiralAttack(getEngine()));
                        } else if (MathUtils.randomBoolean(0.5f)) {
                            AI.TASKS.add(new BossActions.ReverseShiftingSpiralAttack(getEngine()));
                        } else {
                            AI.TASKS.add(new BossActions.LunaticGun(getEngine()));
                        }

                        // Add some spices
                        if (MathUtils.randomBoolean(0.1f)) { // Add some random homing stuff
                            final BossActions.Action ACTION = new BossActions.HomingBulletCircleAttack(getEngine());
                            ACTION.parallel = true;
                            AI.TASKS.add(ACTION);
                        }

                        if (MathUtils.randomBoolean(0.5f)) {
                            AI.TASKS.add(new BossActions.HelixPlusAttack(getEngine(), VIEWPORT));
                        }
                    } else {
                        if (MathUtils.randomBoolean(0.5f))
                            AI.TASKS.add(new BossActions.ImperishableNight(getEngine(), VIEWPORT));
                        else
                            AI.TASKS.add(new BossActions.AsteroidField(getEngine()));
                        AI.TASKS.add(new BossActions.DoNothing(3f));
                    }

                    if (MathUtils.randomBoolean(0.5f)) {
                        Vector2 move = new Vector2();
                        generateRandomMoveTarget(TRANSFORM, move);
                        AI.TASKS.add(new BossActions.Move(0.2f, move));
                    }
                } else {  // > 11.25%
                    // Create a base for all attacks
                    if (MathUtils.randomBoolean(0.66f)) {
                        if (MathUtils.randomBoolean(0.5f)) {
                            AI.TASKS.add(new BossActions.ShiftingSpiralAttack(getEngine()));
                            AI.TASKS.add(new BossActions.ReverseShiftingSpiralAttack(getEngine()));
                            AI.TASKS.get(1).parallel = true;
                        } else {
                            AI.TASKS.add(new BossActions.LunaticGun(getEngine()));
                        }

                        // Add some spices
                        if (MathUtils.randomBoolean(0.1f)) { // Add some random homing stuff
                            final BossActions.Action ACTION = new BossActions.HomingBulletCircleAttack(getEngine());
                            ACTION.parallel = true;
                            AI.TASKS.add(ACTION);
                        }

                        if (MathUtils.randomBoolean(0.5f)) {
                            final BossActions.Action ACTION = new BossActions.TripleLaserBallAttack(getEngine(), VIEWPORT);
                            ACTION.parallel = true;
                            AI.TASKS.add(ACTION);
                        }

                        if (MathUtils.randomBoolean(0.5f)) {
                            AI.TASKS.add(new BossActions.HelixPlusAttack(getEngine(), VIEWPORT));
                        }
                    } else {
                        if (MathUtils.randomBoolean(0.5f))
                            AI.TASKS.add(new BossActions.ImperishableNight(getEngine(), VIEWPORT));
                        else
                            AI.TASKS.add(new BossActions.AsteroidField(getEngine()));
                        AI.TASKS.add(new BossActions.DoNothing(3f));
                    }

                    if (MathUtils.randomBoolean(0.5f)) {
                        Vector2 move = new Vector2();
                        generateRandomMoveTarget(TRANSFORM, move);
                        AI.TASKS.add(new BossActions.Move(0.2f, move));
                    }
                }
                AI.curState = AIState.PROCESSING;
                break;
            case PROCESSING:
                if (AI.TASKS.size > 0) {
                    int i = 0;
                    while (i < AI.TASKS.size) {
                        BossActions.Action a = AI.TASKS.get(i);

                        if (i > 0 && !a.parallel)
                            break;
                        else if (a.act(entity, deltaTime))
                            AI.TASKS.removeIndex(i);
                        else
                            i++;
                    }
                } else {
                    AI.curState = AIState.SCHEDULING;
                }
                break;
            default:
                AI.curState = AIState.SCHEDULING;
        }

        /*switch (AI.state) {
            case -3: { // Determine whether it should chain attacks (if chance fails, reset like normal)
                if (MathUtils.randomBoolean(Interpolation.exp5In.apply(0, .6f, 1f - HEALTH.getHealthPercent()))) { // Will chain attack
                    // region selecting next attack
                    if (HEALTH.getHealthPercent() >= 0.75) { // 75%+
                        AI.state = MathUtils.random(1, AI.ACTIONS.size / 3);
                    } else if (HEALTH.getHealthPercent() >= 0.50) {  // 50% - 75%
                        AI.state = Mathf.biasedRandom(1, AI.ACTIONS.size / 2, 1.5f);
                    } else if (HEALTH.getHealthPercent() >= 0.25) {  // 25% - 50%
                        AI.state = Mathf.biasedRandom(2, MathUtils.round(AI.ACTIONS.size / 1.5f), .75f);
                    } else if (HEALTH.getHealthPercent() >= 0.1125) {  // 11.25% - 25%
                        AI.state = MathUtils.random(1, AI.ACTIONS.size - 1);
                    } else {  // > 11.25%
                        AI.state = Mathf.biasedRandom(1, AI.ACTIONS.size - 1, .3f);
                    }

                    AI.actionTimer = 0;
                    break;
                    // endregion
                } else {
                    AI.state = -2;
                    break;
                }
            }
            case -2: { // reset
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
            }
            case -1: { // Move towards position and choose action
                AI.lerpTimer = MathUtils.clamp(AI.lerpTimer + deltaTime * AI.lerpSpeed, 0, 1);
                float perc = MathUtils.sin(AI.lerpTimer * MathUtils.PI / 2.0f);

                TRANSFORM.POSITION.set(
                        MathUtils.lerp(AI.BEGIN_POS.x, AI.END_POS.x, perc),
                        MathUtils.lerp(AI.BEGIN_POS.y, AI.END_POS.y, perc)
                );

                if (AI.lerpTimer == 1) {
                    // region selecting next attack
                    if (HEALTH.getHealthPercent() >= 0.75) { // 75%+
                        AI.state = *//*AI.ACTIONS.size - 1;*//*MathUtils.random(1, AI.ACTIONS.size / 3); //Uses 1st third
                        AI.lerpSpeed = 1.6f;
                    } else if (HEALTH.getHealthPercent() >= 0.50) {  // 50% - 75%
                        AI.state = Mathf.biasedRandom(1, AI.ACTIONS.size / 2, .5f); //Uses 1st half (biased towards later)
                        AI.lerpSpeed = 2.4f;
                    } else if (HEALTH.getHealthPercent() >= 0.25) {  // 25% - 50%
                        if (MathUtils.randomBoolean(.1f))
                            AI.state = 0; // Fake out
                        else
                            AI.state = Mathf.biasedRandom(2, MathUtils.round(AI.ACTIONS.size / 1.5f), .75f);
                        AI.lerpSpeed = 3.2f;
                    } else if (HEALTH.getHealthPercent() >= 0.1125) {  // 11.25% - 25%
                        if (MathUtils.randomBoolean(.3f))
                            AI.state = 0; // Fake out
                        else
                            AI.state = MathUtils.random(1, AI.ACTIONS.size - 1);
                        AI.lerpSpeed = 4f;
                    } else {  // > 11.25%
                        if (MathUtils.randomBoolean(.5f))
                            AI.state = 0; // Fake out
                        else
                            AI.state = Mathf.biasedRandom(1, AI.ACTIONS.size - 1, .75f);
                        AI.lerpSpeed = MathUtils.random(2f, 4.8f);
                    }
                    // endregion
                }
                break;
            }
            default:
                //do an action
                AI.ACTIONS.get(AI.state).doAction(entity, deltaTime);
        }*/
        // endregion

        if (HEALTH.health <= 0)
            getEngine().removeEntity(entity);
    }

    /**
     * Generates a random target to move towards
     * @param trans     the {@code TransformComponent} whose location and dimensions to use
     * @param returnVec the {@code Vector2} to store the target location in
     * @return a target location that is at least 100 units away from the specified transform
     */
    private Vector2 generateRandomMoveTarget(TransformComponent trans, Vector2 returnVec) {
        do {
            returnVec.set(
                    MathUtils.random(VIEWPORT.getWorldWidth() - trans.SIZE.width),
                    MathUtils.random(VIEWPORT.getWorldHeight() * 2.0f / 3.0f, VIEWPORT.getWorldHeight() - trans.SIZE.height)
            );
        } while (returnVec.dst2(trans.POSITION) <= 1000);

        return returnVec;
    }
}
