package com.coffee.util;

import com.badlogic.ashley.core.Entity;

/**
 * Represents the actions the boss does. Used in {@link com.coffee.entity.systems.AISystem}.
 *
 * @author Phillip O'Reggio
 */
public interface BossActionHandler {
    void doAction(Entity entity, float deltaTime);
}
