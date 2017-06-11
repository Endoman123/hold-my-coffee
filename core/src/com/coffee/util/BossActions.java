package com.coffee.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.coffee.entity.components.AIComponent;
import com.coffee.entity.components.TransformComponent;

/**
 * Class of actions that the boss can do.
 * This allows for some nice control over what the boss does over certain periods.
 *
 */
public class BossActions {

    /**
     * {@link Action Action} that moves the boss to a specified location after a certain wait period.
     */
    public static class Move implements Action {
        private final Vector2 TARGET_LOC;
        private Vector2 beginLoc;
        private boolean moving = false;
        private float actionTimer;

        public Move(float wait, Vector2 loc) {
            actionTimer = wait;

            beginLoc = new Vector2();
            TARGET_LOC = new Vector2(loc);
        }

        @Override
        public boolean act(Entity boss, float deltaTime) {
            final AIComponent AI = Mapper.AI.get(boss);
            final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(boss);

            if (!moving) { // Wait to move
                actionTimer -= deltaTime;
                if (actionTimer <= 0) { // Initialize movement state
                    actionTimer = 0;
                    beginLoc.set(TRANSFORM.POSITION);
                    moving = true;
                }
                return false;
            }

            actionTimer = MathUtils.clamp(actionTimer + deltaTime * AI.lerpSpeed, 0, 1);
            float perc = MathUtils.sin(actionTimer * MathUtils.PI / 2.0f);

            TRANSFORM.POSITION.set(
                    MathUtils.lerp(beginLoc.x, TARGET_LOC.x, perc),
                    MathUtils.lerp(beginLoc.y, TARGET_LOC.y, perc)
            );

            return actionTimer == 1;
        }
    }

    /**
     * Interface intended on creating actions that can be schedule with the boss.
     * This allows for better control over what the boss does/can do during certain stages
     * or events.
     *
     * @author Jared Tulayan
     */
    public interface Action {
        /**
         * Runs the task.
         *
         * @param boss the {@code Entity} that this task is scheduled for
         * @param deltaTime the amount of time passed since last frame
         * @return whether or not the task is finished
         */
        public boolean act(Entity boss, float deltaTime);
    }
}
