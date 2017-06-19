package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
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
                    // region 75%+
                    if (MathUtils.randomBoolean(0.7f)) {
                        Vector2 move = new Vector2();
                        generateRandomMoveTarget(TRANSFORM, move);
                        AI.TASKS.add(new BossActions.Move(1f, move));
                    }

                    // Create a base for all attacks
                    if (MathUtils.randomBoolean(.3f)) {
                        AI.TASKS.add(new BossActions.SimpleSpiralAttack(getEngine()));
                    } else if (MathUtils.randomBoolean(.3f)) {
                        AI.TASKS.add(new BossActions.TempestBloom(getEngine()));
                    } else if (MathUtils.randomBoolean(0.3f)) {
                        AI.TASKS.add(new BossActions.ShotgunSpray(getEngine()));
                    } else if (MathUtils.randomBoolean(0.3f)) {
                        AI.TASKS.add(new BossActions.InvisibleHomingBulletsAttack(getEngine()));
                    }

                    // Add some spices
                    final BossActions.ActionSequence SEQ = new BossActions.ActionSequence(0.3f);
                    int i = 0;
                    while (i < 3) {
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

                    if (MathUtils.randomBoolean(0.5f))
                        AI.TASKS.add(new BossActions.SimpleLaserAttack(getEngine(), VIEWPORT));

                    AI.TASKS.add(new BossActions.DoNothing(2));
                    //endregion
                } else if (HEALTH.getHealthPercent() >= 0.50) {  // 50% - 75
                    // region 50% - 75%
                    if (MathUtils.randomBoolean(0.8f)) {
                        Vector2 move = new Vector2();
                        generateRandomMoveTarget(TRANSFORM, move);
                        AI.TASKS.add(new BossActions.Move(.8f, move));
                    }

                    // Create a base for all attacks
                    if (MathUtils.randomBoolean(0.5f)) {
                        if (MathUtils.randomBoolean(.7f))
                            AI.TASKS.add(new BossActions.SimpleConeAttack(getEngine()));
                        else
                            AI.TASKS.add(new BossActions.ImperishableNight(getEngine()));

                        if (MathUtils.randomBoolean(0.7f)) { // Have a chance that you don't add anything at all
                            BossActions.ActionSequence seq = new BossActions.ActionSequence();
                            seq.parallel = true;
                            seq.addAction(new BossActions.DoNothing(1));
                            if (MathUtils.randomBoolean(0.2f)) // Add some random homing stuff
                                seq.addAction(new BossActions.HomingBulletCircleAttack(getEngine()));
                            else if (MathUtils.randomBoolean(.3f))
                                seq.addAction(new BossActions.PlusBeam(getEngine()));
                            else // Add some random cannon stuff
                                AI.TASKS.add(new BossActions.HelixLaserAttack(getEngine(), VIEWPORT));
                        }

                        if (MathUtils.randomBoolean(0.5f)) {
                            AI.TASKS.add(new BossActions.TripleLaserBallAttack(getEngine(), VIEWPORT));
                        }
                    } else {
                        AI.TASKS.add(new BossActions.SpiralColumnAttack(getEngine()));
                        AI.TASKS.add(new BossActions.DoNothing(5f));
                    }

                    AI.TASKS.add(new BossActions.DoNothing(1.5f));
                    //endregion
                } else if (HEALTH.getHealthPercent() >= 0.25) {  // 25% - 50%
                    // region 25% - 50%
                    // Create a base for all attacks
                    if (MathUtils.randomBoolean(.5f)) {
                        // Fake out chance (high in this stage)
                    } else if (MathUtils.randomBoolean(0.66f)) {
                        if (MathUtils.randomBoolean(0.33f)) {
                            AI.TASKS.add(new BossActions.ShiftingSpiralAttack(getEngine()));
                        } else if (MathUtils.randomBoolean(0.5f)) {
                            AI.TASKS.add(new BossActions.ReverseShiftingSpiralAttack(getEngine()));
                        } else if (MathUtils.randomBoolean(0.4f)) {
                            AI.TASKS.add(new BossActions.ImperishableNight(getEngine()));
                        } else {
                            AI.TASKS.add(new BossActions.LunaticGun(getEngine()));
                        }

                        // Add some spices
                        if (MathUtils.randomBoolean(0.1f)) { // Add some random homing stuff
                            final BossActions.Action ACTION = new BossActions.HomingBulletCircleAttack(getEngine());
                            ACTION.parallel = true;
                            AI.TASKS.add(ACTION);
                        }

                        if (MathUtils.randomBoolean(0.2f)) {
                            AI.TASKS.add(new BossActions.HelixPlusAttack(getEngine(), VIEWPORT));
                        }

                        if (MathUtils.randomBoolean(0.4f)) {
                            AI.TASKS.add(new BossActions.XBeam(getEngine()));
                        }

                        if (MathUtils.randomBoolean(0.4f)) {
                            AI.TASKS.add(new BossActions.PlusBeam(getEngine()));
                        }
                    } else if (MathUtils.randomBoolean(.35f)) {
                        if (MathUtils.randomBoolean(0.5f))
                            AI.TASKS.add(new BossActions.ImperishableNight(getEngine()));
                        else if (MathUtils.randomBoolean(0.4f))
                            AI.TASKS.add(new BossActions.AsteroidField(getEngine()));
                        else {
                            AI.TASKS.add(new BossActions.XBeam(getEngine()));

                            Vector2 move = new Vector2();

                            generateRandomMoveTarget(TRANSFORM, move);
                            AI.TASKS.add(new BossActions.Move(3f, move.cpy()));
                            AI.TASKS.add(new BossActions.XBeam(getEngine()));

                            generateRandomMoveTarget(TRANSFORM, move);
                            AI.TASKS.add(new BossActions.Move(4f, move.cpy()));

                            AI.TASKS.add(new BossActions.AsteroidField(getEngine()));

                            AI.TASKS.add(new BossActions.DoNothing(2f));
                        }

                        AI.TASKS.add(new BossActions.DoNothing(3f));
                    }

                    if (MathUtils.randomBoolean(0.5f)) {
                        Vector2 move = new Vector2();
                        generateRandomMoveTarget(TRANSFORM, move);
                        AI.TASKS.add(new BossActions.Move(1.6f, move));
                    }
                    //endregion
                } else {  // > 25%
                    //region > 25%
                    // Create a base for all attacks
                    if (MathUtils.randomBoolean(0.66f)) {
                        if (MathUtils.randomBoolean(0.4f)) {
                            AI.TASKS.add(new BossActions.SpringBlossom(getEngine()));
                        } else if (MathUtils.randomBoolean(0.4f)) {
                            AI.TASKS.add(new BossActions.LunaticGun(getEngine()));
                        } else if (MathUtils.randomBoolean(0.4f)) {
                            AI.TASKS.add(new BossActions.ShiftingSpiralAttack(getEngine()));
                            AI.TASKS.add(new BossActions.ReverseShiftingSpiralAttack(getEngine()));
                            AI.TASKS.get(1).parallel = true;
                        } else if (MathUtils.randomBoolean(.5f)) {
                            AI.TASKS.add(new BossActions.SimpleSpiralAttack(getEngine()));
                            AI.TASKS.add(new BossActions.SpringBlossom(getEngine()));
                            AI.TASKS.get(1).parallel = true;
                        } else if (MathUtils.randomBoolean(.7f)) {
                            AI.TASKS.add(new BossActions.TempestBloom(getEngine()));
                            AI.TASKS.add(new BossActions.SpringBlossom(getEngine()));
                            AI.TASKS.get(1).parallel = true;
                        }

                        // Add some spices
                        if (MathUtils.randomBoolean(0.3f)) { // Add some random homing stuff
                            final BossActions.Action ACTION = new BossActions.HomingBulletCircleAttack(getEngine());
                            ACTION.parallel = true;
                            AI.TASKS.add(ACTION);
                        }

                        if (MathUtils.randomBoolean(0.3f)) {
                            final BossActions.Action ACTION = new BossActions.XBeam(getEngine());
                            ACTION.parallel = true;
                            AI.TASKS.add(ACTION);
                        }

                        if (MathUtils.randomBoolean(0.3f)) {
                            final BossActions.Action ACTION = new BossActions.StarBeam(getEngine());
                            ACTION.parallel = true;
                            AI.TASKS.add(ACTION);
                        }

                        if (MathUtils.randomBoolean(0.4f)) {
                            final BossActions.Action ACTION = new BossActions.QuadLaserBallAttack(getEngine(), 0);
                            ACTION.parallel = true;
                            AI.TASKS.add(ACTION);
                        }

                        if (MathUtils.randomBoolean(0.3f)) {
                            AI.TASKS.add(new BossActions.HelixPlusAttack(getEngine(), VIEWPORT));
                        } else if (MathUtils.randomBoolean(0.2f)) {
                            AI.TASKS.add(new BossActions.XBeam(getEngine()));
                            AI.TASKS.add(new BossActions.PlusBeam(getEngine()));
                            AI.TASKS.add(new BossActions.StarBeam(getEngine()));
                        }

                    } else if (MathUtils.randomBoolean(.5f)) {
                        if (MathUtils.randomBoolean(0.4f))
                            AI.TASKS.add(new BossActions.ImperishableNight(getEngine()));
                        else if (MathUtils.randomBoolean(.7f)) {
                            AI.TASKS.add(new BossActions.XBeam(getEngine()));

                            Vector2 move = new Vector2();

                            generateRandomMoveTarget(TRANSFORM, move);
                            AI.TASKS.add(new BossActions.Move(3f, move.cpy()));
                            AI.TASKS.add(new BossActions.PlusBeam(getEngine()));

                            generateRandomMoveTarget(TRANSFORM, move);
                            AI.TASKS.add(new BossActions.Move(4f, move.cpy()));
                            AI.TASKS.add(new BossActions.XBeam(getEngine()));

                            generateRandomMoveTarget(TRANSFORM, move);
                            AI.TASKS.add(new BossActions.Move(5f, move.cpy()));
                            AI.TASKS.add(new BossActions.StarBeam(getEngine()));

                            generateRandomMoveTarget(TRANSFORM, move);
                            AI.TASKS.add(new BossActions.Move(6f, move.cpy()));
                            AI.TASKS.add(new BossActions.StarBeam(getEngine()));

                            generateRandomMoveTarget(TRANSFORM, move);
                            AI.TASKS.add(new BossActions.Move(7f, move.cpy()));
                            AI.TASKS.add(new BossActions.HelixLaserAttack(getEngine(), VIEWPORT));
                            AI.TASKS.add(new BossActions.DoNothing(3f));
                        } else
                            AI.TASKS.add(new BossActions.AsteroidField(getEngine()));
                        AI.TASKS.add(new BossActions.DoNothing(3f));
                    }

                    if (MathUtils.randomBoolean(.5f)) {
                        if (MathUtils.randomBoolean(.2f)) {
                            Vector2 move = new Vector2();
                            generateRandomMoveTarget(TRANSFORM, move);
                            AI.TASKS.add(new BossActions.Move(1f, move));
                            final BossActions.Action ACTION = new BossActions.PlusBeam(getEngine());
                            ACTION.parallel = true;
                            AI.TASKS.add(ACTION);
                        } else {
                            Vector2 move = new Vector2();
                            generateRandomMoveTarget(TRANSFORM, move);
                            AI.TASKS.add(new BossActions.Move(2f, move));
                        }
                    }

                    //endregion
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
        // endregion

        //color change of core
        Mapper.SPRITE.get(entity).SPRITES.get(1).setColor(new Color(.2f, .2f, 1, 1).lerp(Color.RED, 1f - HEALTH.getHealthPercent()));

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
