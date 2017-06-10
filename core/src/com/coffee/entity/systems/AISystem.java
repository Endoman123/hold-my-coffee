package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
            case -3: { //Determine whether it should chain attacks (if chance fails, reset like normal)
                if (MathUtils.randomBoolean(Interpolation.exp5In.apply(0, .6f, 1f - HEALTH.getHealthPercent()))) { // Will chain attack
                    // region selecting next attack
                    if (HEALTH.getHealthPercent() >= 0.75) { // 75%+
                        AI.state = MathUtils.random(1, AI.ACTIONS.size / 3);
                    } else if (HEALTH.getHealthPercent() >= 0.50) {  // 50% - 75%
                        AI.state = biasedRandom(1, AI.ACTIONS.size / 2, 1.5f);
                    } else if (HEALTH.getHealthPercent() >= 0.25) {  // 25% - 50%
                        AI.state = biasedRandom(2, MathUtils.round(AI.ACTIONS.size / 1.5f), .75f);
                    } else if (HEALTH.getHealthPercent() >= 0.1125) {  // 11.25% - 25%
                        AI.state = MathUtils.random(1, AI.ACTIONS.size - 1);
                        //B
                    } else {  // > 11.25%
                        AI.state = biasedRandom(1, AI.ACTIONS.size - 1, .3f);
                    }

                    AI.actionTimer = 0;
                    break;
                    //endregion
                } else {
                    AI.state = -2;
                    break;
                }
            }
            case -2: { //reset
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
            case -1: { //move towards position and choose action
                AI.lerpTimer = MathUtils.clamp(AI.lerpTimer + deltaTime * AI.lerpSpeed, 0, 1);
                float perc = MathUtils.sin(AI.lerpTimer * MathUtils.PI / 2.0f);

                TRANSFORM.POSITION.set(
                        MathUtils.lerp(AI.BEGIN_POS.x, AI.END_POS.x, perc),
                        MathUtils.lerp(AI.BEGIN_POS.y, AI.END_POS.y, perc)
                );

                if (AI.lerpTimer == 1) {
                    // region selecting next attack
                    if (HEALTH.getHealthPercent() >= 0.75) { // 75%+
                        AI.state = MathUtils.random(1, AI.ACTIONS.size / 3); //Uses 1st third
                        AI.lerpSpeed = 1.6f;
                    } else if (HEALTH.getHealthPercent() >= 0.50) {  // 50% - 75%
                        AI.state = biasedRandom(1, AI.ACTIONS.size / 2, .5f); //Uses 1st half (biased towards later)
                        AI.lerpSpeed = 2.4f;
                    } else if (HEALTH.getHealthPercent() >= 0.25) {  // 25% - 50%
                        if (MathUtils.randomBoolean(.1f))
                            AI.state = 0; //Fake out
                        else
                            AI.state = biasedRandom(2, MathUtils.round(AI.ACTIONS.size / 1.5f), .75f);
                        AI.lerpSpeed = 3.2f;
                    } else if (HEALTH.getHealthPercent() >= 0.1125) {  // 11.25% - 25%
                        if (MathUtils.randomBoolean(.3f))
                            AI.state = 0; //Fake out
                        else
                            AI.state = MathUtils.random(1, AI.ACTIONS.size - 1);
                        AI.lerpSpeed = 4f;
                    } else {  // > 11.25%
                        if (MathUtils.randomBoolean(.5f))
                            AI.state = 0; //Fake out
                        else
                            AI.state = biasedRandom(1, AI.ACTIONS.size - 1, .75f);
                        AI.lerpSpeed = MathUtils.random(2f, 4.8f);
                    }
                    //endregion
                }
                break;
            }
            default:
                //do an action
                AI.ACTIONS.get(AI.state).doAction(entity, deltaTime);
        }

        // endregion

        if (HEALTH.health <= 0)
            getEngine().removeEntity(entity);
    }

    /**
     * Generates a biased random number.
     * @param min minimum range
     * @param max maximum range
     * @param bias favors lower end if bias > 1. Favors upper end if bias < 1.
     */
    private int biasedRandom(int min, int max, float bias) { //Dunno where else to put this JAROD
        float r = MathUtils.random();    // random between 0 and 1
        r = (float) Math.pow(r, bias);
        return Math.round(min + (max - min) * r);
    }
}
