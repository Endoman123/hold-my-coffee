package com.coffee.util;

import com.badlogic.ashley.core.Entity;

/**
 * Class of actions that the boss can do.
 * This allows for some nice control over what the boss does over certain periods.
 *
 */
public class BossActions {





    /**
     * Interface intended on creating actions that can be schedule with the boss.
     * This allows for better control over what the boss does/can do during certain stages
     * or events.
     *
     * @author Jared Tulayan
     */
    public interface Action {
        public boolean act(Entity boss, float deltaTime);
    }
}
