package com.coffee.util;

import com.badlogic.ashley.core.Entity;

/**
 * @author Phillip O'Reggio
 */
public interface CollisionHandler {
    void enterCollision(Entity entity);
    void whileCollision(Entity entity);
    void exitCollision(Entity entity);
}
